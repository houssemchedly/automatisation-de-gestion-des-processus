package idvey.testapi.tache;

import idvey.testapi.blocage.Blocage;
import idvey.testapi.user.User;
import idvey.testapi.user_story.UserStory;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tache {

    @Id
    @GeneratedValue
    private Integer id;
    private String titre;
    private String description;
    private Double estimation;
    private tachestatut statut;
    @ManyToOne
    @JoinColumn(name = "assigneA_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "UserStor y_id")
    private UserStory userStory;
    @OneToOne(mappedBy = "tache", cascade = CascadeType.ALL)
    private Blocage blocage;
}
