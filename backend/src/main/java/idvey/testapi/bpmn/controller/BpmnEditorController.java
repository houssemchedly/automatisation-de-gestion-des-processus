package idvey.testapi.bpmn.controller;

import idvey.testapi.bpmn.dto.BpmnEditorRequest;
import idvey.testapi.bpmn.dto.BpmnModelResponse;
import idvey.testapi.bpmn.service.BpmnModelService;
import idvey.testapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/bpmn/editor")
@RequiredArgsConstructor
public class BpmnEditorController {

    private final BpmnModelService bpmnModelService;
    private final UserRepository userRepository;

    /**
     * Save BPMN editor content to model
     */
    @PostMapping("/{id}/save")
    @PreAuthorize("hasRole('SCRUM_MASTER')")
    public ResponseEntity<BpmnModelResponse> saveBpmnEditor(
            @PathVariable Long id,
            @RequestBody BpmnEditorRequest request,
            Principal principal) {
        var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var bpmnRequest = idvey.testapi.bpmn.dto.BpmnModelRequest.builder()
                .bpmnXml(request.getBpmnXml())
                .diagram(request.getDiagram())
                .build();

        BpmnModelResponse response = bpmnModelService.updateBpmnModel(id, bpmnRequest, user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Get BPMN XML for editor
     */
    @GetMapping("/{id}/xml")
    public ResponseEntity<String> getBpmnXml(@PathVariable Long id) {
        BpmnModelResponse model = bpmnModelService.getBpmnModelById(id);
        return ResponseEntity.ok(model.getBpmnXml());
    }

    /**
     * Validate BPMN XML
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateBpmnXml(@RequestBody BpmnEditorRequest request) {
        try {
            // Basic validation - check if XML is well-formed
            validateXmlStructure(request.getBpmnXml());
            return ResponseEntity.ok(ValidationResponse.builder()
                    .valid(true)
                    .message("BPMN XML is valid")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ValidationResponse.builder()
                    .valid(false)
                    .message("Invalid BPMN XML: " + e.getMessage())
                    .build());
        }
    }

    private void validateXmlStructure(String bpmnXml) throws Exception {
        // Simple validation - would use XML parser in production
        if (!bpmnXml.contains("<bpmn2:definitions") || !bpmnXml.contains("</bpmn2:definitions>")) {
            throw new Exception("Missing BPMN definitions");
        }
    }

    @lombok.Getter
    @lombok.Setter
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class ValidationResponse {
        private boolean valid;
        private String message;
    }
}
