package idvey.testapi.bpmn.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnModelResponse {
    private Long id;
    private String name;
    private String key;
    private String description;
    private String bpmnXml;
    private String diagram;
    private String status;
    private String deploymentId;
    private Long version;
    private String createdBy;
    private LocalDateTime createdDate;
}
