package idvey.testapi.sprint;

import idvey.testapi.common.BaseEntity;
import idvey.testapi.projet.Projet;
import idvey.testapi.sprint_backlog.SprintBacklog;
import idvey.testapi.user_story.UserStory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sprint")
public class Sprint {

    @Id
    @GeneratedValue
    private Integer id;

    private LocalDate dateDebut;

    private String nom;

    private LocalDate dateFin;

    @Enumerated(EnumType.ORDINAL)
    private sprintstatut statut;

    @ManyToOne
    @JoinColumn(name = "projet_id")
    private Projet projet;

    @ManyToOne
    @JoinColumn(name = "sprint_backlog_id")
    private SprintBacklog sprintBacklog;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL)
    private List<UserStory> userStories;

    private String objectif;
}
