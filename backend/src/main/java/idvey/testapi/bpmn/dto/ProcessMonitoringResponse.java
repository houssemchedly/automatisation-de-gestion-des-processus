package idvey.testapi.bpmn.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessMonitoringResponse {
    private Long processInstanceId;
    private String camundaProcessInstanceId;
    private String processKey;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String startedBy;
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private List<ProcessTaskDetail> tasks;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProcessTaskDetail {
        private String taskId;
        private String taskName;
        private String status;
        private String assignedTo;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
    }
}
