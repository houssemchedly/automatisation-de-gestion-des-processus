package idvey.testapi.bpmn.controller;

import idvey.testapi.bpmn.dto.BpmnEditorRequest;
import idvey.testapi.bpmn.dto.BpmnModelResponse;
import idvey.testapi.bpmn.service.BpmnModelService;
import idvey.testapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
    @PreAuthorize("hasAuthority('SCRUM_MASTER')")
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
            validateXmlStructure(request.getBpmnXml());
            return ResponseEntity.ok(ValidationResponse.builder()
                    .valid(true)
                    .message("BPMN XML is valid")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ValidationResponse.builder()
                    .valid(false)
                    .message("Invalid BPMN XML: " + e.getMessage())
                    .build());
        }
    }

    private void validateXmlStructure(String bpmnXml) throws Exception {
        try {
            Document document = createDocument(bpmnXml);

            var root = document.getDocumentElement();
            if (root == null || !"definitions".equals(root.getLocalName())) {
                throw new Exception("Missing BPMN definitions root element");
            }

            String namespace = root.getNamespaceURI();
            if (namespace == null || !(namespace.contains("http://www.omg.org/spec/BPMN/20100524/MODEL")
                    || namespace.contains("http://bpmn.io/schema/bpmn"))) {
                throw new Exception("Invalid BPMN namespace");
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new Exception("Malformed BPMN XML: " + e.getMessage());
        }
    }

    private Document createDocument(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        var builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlContent)));
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
