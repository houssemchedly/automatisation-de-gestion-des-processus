package idvey.testapi.bpmn.controller;

import idvey.testapi.bpmn.dto.BpmnModelRequest;
import idvey.testapi.bpmn.dto.BpmnModelResponse;
import idvey.testapi.bpmn.service.BpmnModelService;
import idvey.testapi.security.JwtService;
import idvey.testapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/bpmn/models")
@RequiredArgsConstructor
public class BpmnModelController {

    private final BpmnModelService bpmnModelService;
    private final UserRepository userRepository;

    /**
     * Create a new BPMN model (only Scrum Master)
     */
    @PostMapping
    @PreAuthorize("hasRole('SCRUM_MASTER')")
    public ResponseEntity<BpmnModelResponse> createBpmnModel(
            @RequestBody BpmnModelRequest request,
            Principal principal) {
        var user = userRepository.findByNom(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BpmnModelResponse response = bpmnModelService.saveBpmnModel(request, user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Update BPMN model (only creator and Scrum Master)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SCRUM_MASTER')")
    public ResponseEntity<BpmnModelResponse> updateBpmnModel(
            @PathVariable Long id,
            @RequestBody BpmnModelRequest request,
            Principal principal) {
        var user = userRepository.findByNom(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BpmnModelResponse response = bpmnModelService.updateBpmnModel(id, request, user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Deploy BPMN model to Camunda engine (only Scrum Master)
     */
    @PostMapping("/{id}/deploy")
    @PreAuthorize("hasRole('SCRUM_MASTER')")
    public ResponseEntity<BpmnModelResponse> deployBpmnModel(
            @PathVariable Long id,
            Principal principal) {
        var user = userRepository.findByNom(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        BpmnModelResponse response = bpmnModelService.deployBpmnModel(id, user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Get BPMN model by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BpmnModelResponse> getBpmnModel(@PathVariable Long id) {
        BpmnModelResponse response = bpmnModelService.getBpmnModelById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get BPMN model by key
     */
    @GetMapping("/key/{key}")
    public ResponseEntity<BpmnModelResponse> getBpmnModelByKey(@PathVariable String key) {
        BpmnModelResponse response = bpmnModelService.getBpmnModelByKey(key);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all active BPMN models
     */
    @GetMapping("/active")
    public ResponseEntity<List<BpmnModelResponse>> getActiveBpmnModels() {
        List<BpmnModelResponse> response = bpmnModelService.getActiveBpmnModels();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all BPMN models created by current user
     */
    @GetMapping("/my-models")
    public ResponseEntity<List<BpmnModelResponse>> getUserBpmnModels(Principal principal) {
        var user = userRepository.findByNom(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<BpmnModelResponse> response = bpmnModelService.getUserBpmnModels(user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Delete BPMN model (only Scrum Master)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SCRUM_MASTER')")
    public ResponseEntity<Void> deleteBpmnModel(
            @PathVariable Long id,
            Principal principal) {
        var user = userRepository.findByNom(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        bpmnModelService.deleteBpmnModel(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
