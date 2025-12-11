package idvey.testapi.bpmn.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessInstanceResponse {
    private Long id;
    private String camundaProcessInstanceId;
    private String bpmnModelName;
    private String status;
    private String startedBy;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
