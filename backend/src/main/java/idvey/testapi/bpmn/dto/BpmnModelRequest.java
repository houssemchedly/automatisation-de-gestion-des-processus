package idvey.testapi.bpmn.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnModelRequest {
    private String name;
    private String key;
    private String description;
    private String bpmnXml;
    private String diagram;
}
