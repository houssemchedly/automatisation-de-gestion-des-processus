package idvey.testapi.bpmn.repository;

import idvey.testapi.bpmn.BpmnModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BpmnModelRepository extends JpaRepository<BpmnModel, Long> {
    Optional<BpmnModel> findByKey(String key);
    List<BpmnModel> findByStatus(BpmnModel.BpmnStatus status);

    @Query("SELECT b FROM BpmnModel b WHERE b.createdBy.id = :userId ORDER BY b.id DESC")
    List<BpmnModel> findByCreatedByIdOrderByCreatedDateDesc(@Param("userId") Integer userId);
}
