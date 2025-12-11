package idvey.testapi.projet;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjetRepository extends JpaRepository<Projet, Integer>, JpaSpecificationExecutor<Projet>{
    @Query("""
        SELECT DISTINCT projet
        FROM Projet projet
       
        """)
    Page<Projet> findAllDisplayableProjets(Pageable pageable, @Param("userId") Integer userId);

    @Query("""
        SELECT projet
        FROM Projet projet
        WHERE projet.productOwner.id = :ownerId
        """)
    Page<Projet> findAllProjetsByOwner(Pageable pageable, @Param("ownerId") Integer ownerId);

    @Query("""
        SELECT projet
        FROM Projet projet
        WHERE projet.scrumMaster.id = :scrumMasterId
        """)
    Page<Projet> findAllProjetsByScrumMaster(Pageable pageable, @Param("scrumMasterId") Integer scrumMasterId);
}
