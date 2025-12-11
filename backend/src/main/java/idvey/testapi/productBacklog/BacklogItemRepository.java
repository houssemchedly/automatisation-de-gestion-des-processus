package idvey.testapi.productBacklog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BacklogItemRepository extends JpaRepository<BacklogItem, Integer> {

    // Find by product backlog
    Page<BacklogItem> findByProductBacklogId(Integer productBacklogId, Pageable pageable);
    List<BacklogItem> findByProductBacklogId(Integer productBacklogId);

    // Find by status
    Page<BacklogItem> findByStatut(itemstatut statut, Pageable pageable);
    List<BacklogItem> findByStatut(itemstatut statut);

    // Find by priority
    List<BacklogItem> findByProductBacklogIdOrderByPrioriteAsc(Integer productBacklogId);
    List<BacklogItem> findByProductBacklogIdOrderByPrioriteDesc(Integer productBacklogId);

    // Search by title
    @Query("SELECT bi FROM BacklogItem bi WHERE " +
            "LOWER(bi.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(bi.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<BacklogItem> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Advanced search
    @Query("SELECT bi FROM BacklogItem bi WHERE " +
            "(:titre IS NULL OR LOWER(bi.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) AND " +
            "(:statut IS NULL OR bi.statut = :statut) AND " +
            "(:productBacklogId IS NULL OR bi.productBacklog.id = :productBacklogId) AND " +
            "(:minPriorite IS NULL OR bi.priorite >= :minPriorite) AND " +
            "(:maxPriorite IS NULL OR bi.priorite <= :maxPriorite)")
    Page<BacklogItem> findWithFilters(
            @Param("titre") String titre,
            @Param("statut") itemstatut statut,
            @Param("productBacklogId") Integer productBacklogId,
            @Param("minPriorite") Integer minPriorite,
            @Param("maxPriorite") Integer maxPriorite,
            Pageable pageable
    );

    // Count by status for a product backlog
    @Query("SELECT COUNT(bi) FROM BacklogItem bi WHERE bi.productBacklog.id = :productBacklogId AND bi.statut = :statut")
    Long countByProductBacklogIdAndStatut(@Param("productBacklogId") Integer productBacklogId, @Param("statut") itemstatut statut);

    // Get total story points for a product backlog
    @Query("SELECT COALESCE(SUM(bi.points), 0) FROM BacklogItem bi WHERE bi.productBacklog.id = :productBacklogId")
    Integer getTotalPointsByProductBacklogId(@Param("productBacklogId") Integer productBacklogId);

    // Get completed story points for a product backlog - FIXED QUERY
    @Query("SELECT COALESCE(SUM(bi.points), 0) FROM BacklogItem bi WHERE bi.productBacklog.id = :productBacklogId AND bi.statut = :statut")
    Integer getCompletedPointsByProductBacklogId(@Param("productBacklogId") Integer productBacklogId, @Param("statut") itemstatut statut);

    // Find items with highest priority in a product backlog
    Optional<BacklogItem> findFirstByProductBacklogIdOrderByPrioriteDesc(Integer productBacklogId);

    // Check if priority exists in product backlog
    boolean existsByProductBacklogIdAndPriorite(Integer productBacklogId, Integer priorite);
}
