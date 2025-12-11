package idvey.testapi.sprint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SprintRepository extends JpaRepository<Sprint, Integer> {

    List<Sprint> findByStatut(sprintstatut statut);

    @Query("SELECT s FROM Sprint s WHERE s.dateDebut <= :date AND s.dateFin >= :date")
    List<Sprint> findActiveSprintsOnDate(@Param("date") LocalDate date);

    @Query("SELECT s FROM Sprint s WHERE s.dateDebut >= :startDate AND s.dateFin <= :endDate")
    List<Sprint> findSprintsBetweenDates(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    Page<Sprint> findByStatut(sprintstatut statut, Pageable pageable);

    @Query("SELECT s FROM Sprint s WHERE " +
            "(:nom IS NULL OR LOWER(s.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
            "(:statut IS NULL OR s.statut = :statut)")
    Page<Sprint> findWithFilters(@Param("nom") String nom,
                                 @Param("statut") sprintstatut statut,
                                 Pageable pageable);

    List<Sprint> findBySprintBacklogId(Integer sprintBacklogId);

    List<Sprint> findByProjetId(Integer projetId);

    @Query("SELECT s FROM Sprint s WHERE s.sprintBacklog.productBacklog.id = :productBacklogId")
    List<Sprint> findByProductBacklogId(@Param("productBacklogId") Integer productBacklogId);

    @Query("SELECT s FROM Sprint s WHERE s.projet.scrumMaster.id = :scrumMasterId")
    Page<Sprint> findByProjetScrumMasterId(@Param("scrumMasterId") Integer scrumMasterId, Pageable pageable);
}
