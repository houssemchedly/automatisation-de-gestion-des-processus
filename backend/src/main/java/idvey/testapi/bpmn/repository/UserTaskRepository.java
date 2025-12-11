package idvey.testapi.bpmn.repository;

import idvey.testapi.bpmn.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    Optional<UserTask> findByCamundaTaskId(String camundaTaskId);

    List<UserTask> findByAssignedToIdAndStatus(Integer userId, UserTask.TaskStatus status);

    // Orders by id DESC to get most recent tasks first
    @Query("SELECT ut FROM UserTask ut WHERE ut.processInstance.id = :processInstanceId ORDER BY ut.id DESC")
    List<UserTask> findByProcessInstanceIdOrderByCreatedAtDesc(Long processInstanceId);
}
