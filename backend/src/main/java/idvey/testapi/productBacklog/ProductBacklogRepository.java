package idvey.testapi.productBacklog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductBacklogRepository extends JpaRepository<ProductBacklog, Integer>, JpaSpecificationExecutor<ProductBacklog> {

    @Query("""
            SELECT pb
            FROM ProductBacklog pb
            WHERE pb.projet.id = :projectId
            """)
    Optional<ProductBacklog> findByProjectId(@Param("projectId") Integer projectId);

    @Query("""
            SELECT pb
            FROM ProductBacklog pb
            WHERE pb.projet.productOwner.id = :productOwnerId
            """)
    Page<ProductBacklog> findAllByProductOwner(Pageable pageable, @Param("productOwnerId") Integer productOwnerId);


    @Query("SELECT pb FROM ProductBacklog pb LEFT JOIN FETCH pb.sprintBacklogs WHERE pb.id = :id")
    Optional<ProductBacklog> findByIdWithSprintBacklogs(@Param("id") Integer id);

}

