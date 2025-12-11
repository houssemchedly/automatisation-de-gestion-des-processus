package idvey.testapi.productBacklog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import idvey.testapi.projet.Projet;
import idvey.testapi.sprint_backlog.SprintBacklog;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductBacklog {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne
    @JoinColumn(name = "projet_id")
    private Projet projet;
    @OneToMany(mappedBy = "productBacklog", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BacklogItem> items;
    @OneToMany(mappedBy = "productBacklog", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SprintBacklog> sprintBacklogs;

}
