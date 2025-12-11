package idvey.testapi.sprint_backlog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SprintBacklogRepository extends JpaRepository<SprintBacklog, Integer> {

    @Query("SELECT sb FROM SprintBacklog sb JOIN FETCH sb.userStories WHERE sb.id = :id")
    Optional<SprintBacklog> findByIdWithUserStories(@Param("id") Integer id);

    @Query("SELECT sb FROM SprintBacklog sb JOIN FETCH sb.sprints WHERE sb.id = :id")
    Optional<SprintBacklog> findByIdWithSprints(@Param("id") Integer id);

    @Query("SELECT sb FROM SprintBacklog sb " +
            "LEFT JOIN FETCH sb.userStories us " +
            "LEFT JOIN FETCH sb.sprints s")
    List<SprintBacklog> findAllWithDetails();

    @Query("SELECT sb FROM SprintBacklog sb " +
            "LEFT JOIN FETCH sb.userStories us " +
            "LEFT JOIN FETCH sb.sprints s")
    Page<SprintBacklog> findAllWithDetails(Pageable pageable);

    @Query("SELECT COUNT(us) FROM SprintBacklog sb JOIN sb.userStories us WHERE sb.id = :sprintBacklogId")
    Long countUserStoriesBySprintBacklogId(@Param("sprintBacklogId") Integer sprintBacklogId);

    @Query("SELECT COUNT(us) FROM SprintBacklog sb JOIN sb.userStories us " +
            "WHERE sb.id = :sprintBacklogId AND us.statut = idvey.testapi.user_story.storystatut.TERMINE")
    Long countCompletedUserStoriesBySprintBacklogId(@Param("sprintBacklogId") Integer sprintBacklogId);

    @Query("SELECT SUM(us.points) FROM SprintBacklog sb JOIN sb.userStories us WHERE sb.id = :sprintBacklogId")
    Integer sumStoryPointsBySprintBacklogId(@Param("sprintBacklogId") Integer sprintBacklogId);

    @Query("SELECT SUM(us.points) FROM SprintBacklog sb JOIN sb.userStories us " +
            "WHERE sb.id = :sprintBacklogId AND us.statut = idvey.testapi.user_story.storystatut.TERMINE")
    Integer sumCompletedStoryPointsBySprintBacklogId(@Param("sprintBacklogId") Integer sprintBacklogId);

    List<SprintBacklog> findByProductBacklogId(Integer productBacklogId);

    @Query("SELECT sb FROM SprintBacklog sb JOIN FETCH sb.productBacklog WHERE sb.id = :id")
    Optional<SprintBacklog> findByIdWithProductBacklog(@Param("id") Integer id);
}
