package idvey.testapi.projet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import idvey.testapi.common.BaseEntity;
import idvey.testapi.meet.Meeting;
import idvey.testapi.productBacklog.ProductBacklog;
import idvey.testapi.sprint.Sprint;
import idvey.testapi.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;


import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Projet {

    @Id
    @GeneratedValue
    private Integer id;
    @NotNull
    private String nom;
    @NotNull
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private ProjetStatut statut;
    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(
            name = "projet_users",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> equipe;
    private boolean actif;
    @ManyToOne
    @JoinColumn(name = "product_owner")
    @JsonIgnore
    private User productOwner;
    @ManyToOne
    @JoinColumn(name = "scrum_master")
    @JsonIgnore
    private User scrumMaster;
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Sprint> sprints;
    @OneToOne(mappedBy = "projet", cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "product_backlog")
    private ProductBacklog productBacklog;
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Meeting> meetings;

}
