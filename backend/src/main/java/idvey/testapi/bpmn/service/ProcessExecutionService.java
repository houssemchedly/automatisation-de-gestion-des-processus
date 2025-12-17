package idvey.testapi.bpmn.service;

import idvey.testapi.bpmn.BpmnModel;
import idvey.testapi.bpmn.UserTask;
import idvey.testapi.bpmn.dto.ProcessInstanceResponse;
import idvey.testapi.bpmn.repository.BpmnModelRepository;
import idvey.testapi.bpmn.repository.ProcessInstanceRepository;
import idvey.testapi.bpmn.repository.UserTaskRepository;
import idvey.testapi.user.User;
import idvey.testapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessExecutionService {

    private final RuntimeService camundaRuntimeService;
    private final TaskService camundaTaskService;
    private final ProcessInstanceRepository processInstanceRepository;
    private final BpmnModelRepository bpmnModelRepository;
    private final UserRepository userRepository;
    private final UserTaskRepository userTaskRepository;
    private final BpmnModelMapper bpmnModelMapper;

    /**
     * Start a new process instance
     */
    public ProcessInstanceResponse startProcessInstance(String processKey, Integer userId, Map<String, Object> variables) {
        BpmnModel bpmnModel = bpmnModelRepository.findByKey(processKey)
                .orElseThrow(() -> new RuntimeException("BPMN Model not found"));

        if (bpmnModel.getStatus() == BpmnModel.BpmnStatus.DEPLOYED) {
            bpmnModel.setStatus(BpmnModel.BpmnStatus.ACTIVE);
            bpmnModelRepository.save(bpmnModel);
        } else if (bpmnModel.getStatus() != BpmnModel.BpmnStatus.ACTIVE) {
            throw new RuntimeException("Process is not active");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Start Camunda process
        ProcessInstance camundaProcess = camundaRuntimeService.startProcessInstanceByKey(
                processKey,
                variables != null ? variables : new HashMap<>()
        );

        // Save process instance to database
        idvey.testapi.bpmn.ProcessInstance processInstance = idvey.testapi.bpmn.ProcessInstance.builder()
                .camundaProcessInstanceId(camundaProcess.getId())
                .bpmnModel(bpmnModel)
                .startedBy(user)
                .status(idvey.testapi.bpmn.ProcessInstance.ProcessStatus.RUNNING)
                .startedAt(LocalDateTime.now())
                .build();

        processInstanceRepository.save(processInstance);
        return bpmnModelMapper.toProcessInstanceResponse(processInstance);
    }

    /**
     * Get process instance details
     */
    public ProcessInstanceResponse getProcessInstance(Long id) {
        idvey.testapi.bpmn.ProcessInstance processInstance = processInstanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Process instance not found"));
        return bpmnModelMapper.toProcessInstanceResponse(processInstance);
    }

    /**
     * Get all process instances for a BPMN model
     */
    public List<ProcessInstanceResponse> getProcessInstances(Long bpmnModelId) {
        List<idvey.testapi.bpmn.ProcessInstance> instances = processInstanceRepository.findByBpmnModelIdOrderByStartedAtDesc(bpmnModelId);
        return instances.stream()
                .map(bpmnModelMapper::toProcessInstanceResponse)
                .collect(Collectors.toList());
    }

    /**
     * Complete a user task
     */
    public void completeUserTask(String camundaTaskId, Integer userId, Map<String, Object> variables) {
        UserTask userTask = getUserTaskByCamundaId(camundaTaskId);

        if (!userTask.getAssignedTo().getId().equals(userId)) {
            throw new RuntimeException("You are not assigned to this task");
        }

        camundaTaskService.complete(camundaTaskId, variables != null ? variables : new HashMap<>());

        userTask.setStatus(UserTask.TaskStatus.COMPLETED);
        userTask.setCompletedAt(LocalDateTime.now());
        userTaskRepository.save(userTask);
    }

    /**
     * Claim a user task
     */
    public void claimUserTask(String camundaTaskId, Integer userId) {
        camundaTaskService.claim(camundaTaskId, String.valueOf(userId));

        UserTask userTask = getUserTaskByCamundaId(camundaTaskId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userTask.setAssignedTo(user);
        userTask.setStatus(UserTask.TaskStatus.CLAIMED);
        userTask.setClaimedAt(LocalDateTime.now());
        userTaskRepository.save(userTask);
    }

    /**
     * Get user task by Camunda ID
     */
    private UserTask getUserTaskByCamundaId(String camundaTaskId) {
        return userTaskRepository.findByCamundaTaskId(camundaTaskId)
                .orElseThrow(() -> new RuntimeException("User task not found: " + camundaTaskId));
    }
}
