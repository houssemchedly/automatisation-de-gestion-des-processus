package idvey.testapi.bpmn.service;

import idvey.testapi.bpmn.BpmnModel;
import idvey.testapi.bpmn.ProcessInstance;
import idvey.testapi.bpmn.UserTask;
import idvey.testapi.bpmn.dto.BpmnModelResponse;
import idvey.testapi.bpmn.dto.ProcessInstanceResponse;
import idvey.testapi.bpmn.dto.UserTaskResponse;
import org.springframework.stereotype.Component;

@Component
public class BpmnModelMapper {

    public BpmnModelResponse toBpmnModelResponse(BpmnModel model) {
        return BpmnModelResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .key(model.getKey())
                .description(model.getDescription())
                .bpmnXml(model.getBpmnXml())
                .diagram(model.getDiagram())
                .status(model.getStatus().toString())
                .deploymentId(model.getDeploymentId())
                .version(model.getVersion())
                .createdBy(model.getCreatedBy().getFullName())
                .createdDate(model.getCreatedBy().getCreatedDate())
                .build();
    }

    public ProcessInstanceResponse toProcessInstanceResponse(ProcessInstance instance) {
        return ProcessInstanceResponse.builder()
                .id(instance.getId())
                .camundaProcessInstanceId(instance.getCamundaProcessInstanceId())
                .bpmnModelName(instance.getBpmnModel().getName())
                .status(instance.getStatus().toString())
                .startedBy(instance.getStartedBy().getFullName())
                .startedAt(instance.getStartedAt())
                .endedAt(instance.getEndedAt())
                .build();
    }

    public UserTaskResponse toUserTaskResponse(UserTask task) {
        return UserTaskResponse.builder()
                .id(task.getId())
                .camundaTaskId(task.getCamundaTaskId())
                .taskName(task.getTaskName())
                .status(task.getStatus().toString())
                .assignedTo(task.getAssignedTo() != null ? task.getAssignedTo().getFullName() : null)
                .claimedAt(task.getClaimedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }
}
