package idvey.testapi.bpmn.controller;

import idvey.testapi.bpmn.dto.ProcessInstanceResponse;
import idvey.testapi.bpmn.service.ProcessExecutionService;
import idvey.testapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bpmn/processes")
@RequiredArgsConstructor
public class ProcessExecutionController {

    private final ProcessExecutionService processExecutionService;
    private final UserRepository userRepository;

    /**
     * Start a new process instance
     */
    @PostMapping("/start/{processKey}")
    public ResponseEntity<ProcessInstanceResponse> startProcessInstance(
            @PathVariable String processKey,
            @RequestBody(required = false) Map<String, Object> variables,
            Principal principal) {
        var user = userRepository.findByNom(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ProcessInstanceResponse response = processExecutionService.startProcessInstance(processKey, user.getId(), variables);
        return ResponseEntity.ok(response);
    }

    /**
     * Get process instance details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProcessInstanceResponse> getProcessInstance(@PathVariable Long id) {
        ProcessInstanceResponse response = processExecutionService.getProcessInstance(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all process instances for a BPMN model
     */
    @GetMapping("/model/{bpmnModelId}")
    public ResponseEntity<List<ProcessInstanceResponse>> getProcessInstances(@PathVariable Long bpmnModelId) {
        List<ProcessInstanceResponse> response = processExecutionService.getProcessInstances(bpmnModelId);
        return ResponseEntity.ok(response);
    }

    /**
     * Complete a user task
     */
    @PostMapping("/tasks/{camundaTaskId}/complete")
    public ResponseEntity<Void> completeUserTask(
            @PathVariable String camundaTaskId,
            @RequestBody(required = false) Map<String, Object> variables,
            Principal principal) {
        var user = userRepository.findByNom(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        processExecutionService.completeUserTask(camundaTaskId, user.getId(), variables);
        return ResponseEntity.noContent().build();
    }

    /**
     * Claim a user task
     */
    @PostMapping("/tasks/{camundaTaskId}/claim")
    public ResponseEntity<Void> claimUserTask(
            @PathVariable String camundaTaskId,
            Principal principal) {
        var user = userRepository.findByNom(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        processExecutionService.claimUserTask(camundaTaskId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
