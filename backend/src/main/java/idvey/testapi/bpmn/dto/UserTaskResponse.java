package idvey.testapi.bpmn.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTaskResponse {
    private Long id;
    private String camundaTaskId;
    private String taskName;
    private String status;
    private String assignedTo;
    private LocalDateTime claimedAt;
    private LocalDateTime completedAt;
}
