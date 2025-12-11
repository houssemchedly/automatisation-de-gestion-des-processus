package idvey.testapi.bpmn.repository;

import idvey.testapi.bpmn.ProcessInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessInstanceRepository extends JpaRepository<ProcessInstance, Long> {
    Optional<ProcessInstance> findByCamundaProcessInstanceId(String camundaProcessInstanceId);
    List<ProcessInstance> findByBpmnModelIdOrderByStartedAtDesc(Long bpmnModelId);
    List<ProcessInstance> findByStatus(ProcessInstance.ProcessStatus status);
}
