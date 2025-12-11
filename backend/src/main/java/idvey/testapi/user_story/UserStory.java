package idvey.testapi.user_story;

import idvey.testapi.sprint.Sprint;
import idvey.testapi.tache.Tache;
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
public class UserStory {

    @Id
    @GeneratedValue
    private Integer id;
    private String titre;
    private String description;
    private Integer priorite;
    private Integer points;
    private storystatut statut;
    @ManyToOne
    @JoinColumn(name = "Sprint_Backlog_id")
    private SprintBacklog sprintBacklog;
    @ManyToOne
    @JoinColumn(name = "Sprint_id")
    private Sprint sprint;
    @OneToMany(mappedBy = "userStory", cascade = CascadeType.ALL)
    private List<Tache> taches;
}
