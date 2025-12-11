package idvey.testapi.blocage;

import idvey.testapi.tache.Tache;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

public class Blocage {

    @Id
    @GeneratedValue
    private Integer id;
    private String titre;
    private String description;
    private LocalDate dateSignalement;
    private LocalDate dateResolution;
    private blocstatut statut;
    private Integer priorite;
    @OneToOne
    @JoinColumn(name = "tache_id")
    private Tache tache;
    @CollectionTable(name = "blocage_commentaires", joinColumns = @JoinColumn(name = "blocage_id"))
    @Column(name = "commentaire")
    private String commentaires;
}
