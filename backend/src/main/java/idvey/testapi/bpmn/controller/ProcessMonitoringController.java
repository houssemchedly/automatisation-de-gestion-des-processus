package idvey.testapi.bpmn.controller;

import idvey.testapi.bpmn.dto.ProcessMonitoringResponse;
import idvey.testapi.bpmn.service.ProcessMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bpmn/monitoring")
@RequiredArgsConstructor
public class ProcessMonitoringController {

    private final ProcessMonitoringService processMonitoringService;

    /**
     * Get detailed monitoring information for a process instance
     */
    @GetMapping("/processes/{processInstanceId}")
    public ResponseEntity<ProcessMonitoringResponse> getProcessMonitoring(
            @PathVariable Long processInstanceId) {
        ProcessMonitoringResponse response = processMonitoringService.getProcessMonitoring(processInstanceId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get process history
     */
    @GetMapping("/history/{camundaProcessInstanceId}")
    public ResponseEntity<Object> getProcessHistory(
            @PathVariable String camundaProcessInstanceId) {
        Object history = processMonitoringService.getProcessHistory(camundaProcessInstanceId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get process statistics
     */
    @GetMapping("/statistics/{bpmnModelId}")
    public ResponseEntity<ProcessMonitoringService.ProcessStatistics> getProcessStatistics(
            @PathVariable Long bpmnModelId) {
        ProcessMonitoringService.ProcessStatistics stats = processMonitoringService.getProcessStatistics(bpmnModelId);
        return ResponseEntity.ok(stats);
    }
}
