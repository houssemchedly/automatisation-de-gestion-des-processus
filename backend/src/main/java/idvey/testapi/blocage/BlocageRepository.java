package idvey.testapi.blocage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlocageRepository extends JpaRepository<Blocage, Integer> {

    @Query("SELECT b FROM Blocage b WHERE b.statut = :statut")
    Page<Blocage> findByStatut(@Param("statut") blocstatut statut, Pageable pageable);

    @Query("SELECT b FROM Blocage b WHERE b.priorite = :priorite")
    Page<Blocage> findByPriorite(@Param("priorite") Integer priorite, Pageable pageable);

    @Query("SELECT b FROM Blocage b WHERE b.tache.id = :tacheId")
    Page<Blocage> findByTacheId(@Param("tacheId") Integer tacheId, Pageable pageable);

    @Query("SELECT b FROM Blocage b WHERE " +
            "(:titre IS NULL OR LOWER(b.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) AND " +
            "(:statut IS NULL OR b.statut = :statut) AND " +
            "(:priorite IS NULL OR b.priorite = :priorite)")
    Page<Blocage> findBlocagesWithFilters(
            @Param("titre") String titre,
            @Param("statut") blocstatut statut,
            @Param("priorite") Integer priorite,
            Pageable pageable
    );
}
