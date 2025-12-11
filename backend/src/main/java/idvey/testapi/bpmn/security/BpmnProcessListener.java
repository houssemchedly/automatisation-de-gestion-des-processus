package idvey.testapi.bpmn.security;

import idvey.testapi.bpmn.ProcessInstance;
import idvey.testapi.bpmn.UserTask;
import idvey.testapi.bpmn.repository.ProcessInstanceRepository;
import idvey.testapi.bpmn.repository.UserTaskRepository;
import idvey.testapi.user.User;
import idvey.testapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BpmnProcessListener implements TaskListener {

    private final UserTaskRepository userTaskRepository;
    private final ProcessInstanceRepository processInstanceRepository;
    private final UserRepository userRepository;

    /**
     * Listener called when a task is created
     * Syncs Camunda tasks with our database
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        String eventName = delegateTask.getEventName();

        if (EVENTNAME_CREATE.equals(eventName)) {
            handleTaskCreated(delegateTask);
        } else if (EVENTNAME_COMPLETE.equals(eventName)) {
            handleTaskCompleted(delegateTask);
        }
    }

    private void handleTaskCreated(DelegateTask delegateTask) {
        String processInstanceId = delegateTask.getProcessInstanceId();
        ProcessInstance processInstance = processInstanceRepository.findByCamundaProcessInstanceId(processInstanceId)
                .orElse(null);

        if (processInstance != null) {
            UserTask userTask = UserTask.builder()
                    .camundaTaskId(delegateTask.getId())
                    .processInstance(processInstance)
                    .taskName(delegateTask.getName())
                    .status(UserTask.TaskStatus.PENDING)
                    .build();

            userTaskRepository.save(userTask);
        }
    }

    private void handleTaskCompleted(DelegateTask delegateTask) {
        UserTask userTask = userTaskRepository.findByCamundaTaskId(delegateTask.getId())
                .orElse(null);

        if (userTask != null) {
            userTask.setStatus(UserTask.TaskStatus.COMPLETED);
            userTask.setCompletedAt(LocalDateTime.now());
            userTaskRepository.save(userTask);
        }
    }
}
