package idvey.testapi.bpmn.service;

import idvey.testapi.bpmn.ProcessInstance;
import idvey.testapi.bpmn.UserTask;
import idvey.testapi.bpmn.dto.ProcessMonitoringResponse;
import idvey.testapi.bpmn.repository.ProcessInstanceRepository;
import idvey.testapi.bpmn.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProcessMonitoringService {

    private final ProcessInstanceRepository processInstanceRepository;
    private final UserTaskRepository userTaskRepository;
    private final HistoryService camundaHistoryService;

    /**
     * Get detailed monitoring information for a process instance
     */
    public ProcessMonitoringResponse getProcessMonitoring(Long processInstanceId) {
        ProcessInstance processInstance = processInstanceRepository.findById(processInstanceId)
                .orElseThrow(() -> new RuntimeException("Process instance not found"));

        List<UserTask> tasks = userTaskRepository.findByProcessInstanceIdOrderByCreatedAtDesc(processInstanceId);

        long completedTasks = tasks.stream()
                .filter(t -> t.getStatus() == UserTask.TaskStatus.COMPLETED)
                .count();

        long pendingTasks = tasks.stream()
                .filter(t -> t.getStatus() == UserTask.TaskStatus.PENDING)
                .count();

        List<ProcessMonitoringResponse.ProcessTaskDetail> taskDetails = tasks.stream()
                .map(task -> ProcessMonitoringResponse.ProcessTaskDetail.builder()
                        .taskId(task.getCamundaTaskId())
                        .taskName(task.getTaskName())
                        .status(task.getStatus().toString())
                        .assignedTo(task.getAssignedTo() != null ? task.getAssignedTo().getFullName() : "Unassigned")
                        .createdAt(task.getClaimedAt())
                        .completedAt(task.getCompletedAt())
                        .build())
                .collect(Collectors.toList());

        return ProcessMonitoringResponse.builder()
                .processInstanceId(processInstance.getId())
                .camundaProcessInstanceId(processInstance.getCamundaProcessInstanceId())
                .processKey(processInstance.getBpmnModel().getKey())
                .status(processInstance.getStatus().toString())
                .startedAt(processInstance.getStartedAt())
                .endedAt(processInstance.getEndedAt())
                .startedBy(processInstance.getStartedBy().getFullName())
                .totalTasks(tasks.size())
                .completedTasks((int) completedTasks)
                .pendingTasks((int) pendingTasks)
                .tasks(taskDetails)
                .build();
    }

    /**
     * Get process instance history from Camunda
     */
    public Object getProcessHistory(String camundaProcessInstanceId) {
        HistoricProcessInstance historicProcess = camundaHistoryService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(camundaProcessInstanceId)
                .singleResult();

        return historicProcess;
    }

    /**
     * Get process statistics
     */
    public ProcessStatistics getProcessStatistics(Long bpmnModelId) {
        List<ProcessInstance> instances = processInstanceRepository.findByBpmnModelIdOrderByStartedAtDesc(bpmnModelId);

        long completed = instances.stream()
                .filter(p -> p.getStatus() == ProcessInstance.ProcessStatus.COMPLETED)
                .count();

        long running = instances.stream()
                .filter(p -> p.getStatus() == ProcessInstance.ProcessStatus.RUNNING)
                .count();

        long suspended = instances.stream()
                .filter(p -> p.getStatus() == ProcessInstance.ProcessStatus.SUSPENDED)
                .count();

        return ProcessStatistics.builder()
                .totalInstances(instances.size())
                .completedInstances((int) completed)
                .runningInstances((int) running)
                .suspendedInstances((int) suspended)
                .averageDuration(calculateAverageDuration(instances))
                .build();
    }

    private long calculateAverageDuration(List<ProcessInstance> instances) {
        List<ProcessInstance> completedInstances = instances.stream()
                .filter(p -> p.getStatus() == ProcessInstance.ProcessStatus.COMPLETED)
                .collect(Collectors.toList());

        if (completedInstances.isEmpty()) {
            return 0;
        }

        long totalDuration = completedInstances.stream()
                .mapToLong(p -> java.time.temporal.ChronoUnit.MILLIS.between(
                        p.getStartedAt().atZone(ZoneId.systemDefault()),
                        p.getEndedAt().atZone(ZoneId.systemDefault())
                ))
                .sum();

        return totalDuration / completedInstances.size();
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ProcessStatistics {
        private int totalInstances;
        private int completedInstances;
        private int runningInstances;
        private int suspendedInstances;
        private long averageDuration;
    }
}
