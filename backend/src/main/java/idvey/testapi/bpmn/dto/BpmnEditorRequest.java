package idvey.testapi.bpmn.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmnEditorRequest {
    private String bpmnXml;
    private String diagram;
}
