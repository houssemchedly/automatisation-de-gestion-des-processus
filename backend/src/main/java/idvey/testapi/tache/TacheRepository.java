package idvey.testapi.tache;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TacheRepository extends JpaRepository<Tache, Integer> {

    Page<Tache> findByStatut(tachestatut statut, Pageable pageable);

    Page<Tache> findByUserId(Integer userId, Pageable pageable);

    Page<Tache> findByUserStoryId(Integer userStoryId, Pageable pageable);

    @Query("SELECT t FROM Tache t WHERE " +
            "(:titre IS NULL OR LOWER(t.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) AND " +
            "(:statut IS NULL OR t.statut = :statut) AND " +
            "(:userId IS NULL OR t.user.id = :userId) AND " +
            "(:userStoryId IS NULL OR t.userStory.id = :userStoryId)")
    Page<Tache> findWithFilters(@Param("titre") String titre,
                                @Param("statut") tachestatut statut,
                                @Param("userId") Integer userId,
                                @Param("userStoryId") Integer userStoryId,
                                Pageable pageable);

    List<Tache> findByUserIdAndStatut(Integer userId, tachestatut statut);

    @Query("SELECT COUNT(t) FROM Tache t WHERE t.statut = :statut")
    Long countByStatut(@Param("statut") tachestatut statut);

    @Query("SELECT AVG(t.estimation) FROM Tache t WHERE t.statut = :statut")
    Double averageEstimationByStatut(@Param("statut") tachestatut statut);
}


