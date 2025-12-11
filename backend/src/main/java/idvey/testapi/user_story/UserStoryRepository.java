package idvey.testapi.user_story;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserStoryRepository extends JpaRepository<UserStory, Integer> {

    List<UserStory> findByStatut(storystatut statut);

    @Query("SELECT us FROM UserStory us WHERE us.sprintBacklog.id = :sprintBacklogId")
    List<UserStory> findBySprintBacklogId(@Param("sprintBacklogId") Integer sprintBacklogId);

    @Query("SELECT us FROM UserStory us WHERE " +
            "(:titre IS NULL OR LOWER(us.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) AND " +
            "(:statut IS NULL OR us.statut = :statut) AND " +
            "(:priorite IS NULL OR us.priorite = :priorite) AND " +
            "(:sprintBacklogId IS NULL OR us.sprintBacklog.id = :sprintBacklogId)")
    Page<UserStory> findWithFilters(@Param("titre") String titre,
                                    @Param("statut") storystatut statut,
                                    @Param("priorite") Integer priorite,
                                    @Param("sprintBacklogId") Integer sprintBacklogId,
                                    Pageable pageable);

    @Query("SELECT us FROM UserStory us ORDER BY us.priorite ASC")
    List<UserStory> findAllOrderByPriorite();

    @Query("SELECT COUNT(us) FROM UserStory us WHERE us.statut = :statut")
    long countByStatut(@Param("statut") storystatut statut);
}
