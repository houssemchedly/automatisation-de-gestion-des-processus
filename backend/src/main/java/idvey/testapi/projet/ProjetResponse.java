package idvey.testapi.projet;

import idvey.testapi.productBacklog.ProductBacklog;
import idvey.testapi.user.User;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjetResponse {

    private Integer id;
    private String nom;
    private String description;
    private User productOwner;
    private User scrumMaster;
    private ProjetStatut statut;
    private List<User> equipe;
    private boolean actif;
    private LocalDate dateDebut;
    private ProductBacklog productBacklog;
}
