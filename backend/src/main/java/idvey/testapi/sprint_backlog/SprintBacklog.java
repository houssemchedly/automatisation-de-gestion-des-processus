package idvey.testapi.sprint_backlog;

import idvey.testapi.productBacklog.ProductBacklog;
import idvey.testapi.sprint.Sprint;
import idvey.testapi.user_story.UserStory;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SprintBacklog {

    @Id
    @GeneratedValue
    private Integer id;
    @OneToMany(mappedBy = "sprintBacklog", cascade = CascadeType.ALL)
    private List<Sprint> sprints;
    @OneToMany(mappedBy = "sprintBacklog", cascade = CascadeType.ALL)
    private List<UserStory> userStories;
    @ManyToOne
    @JoinColumn(name = "product_backlog_id")
    private ProductBacklog productBacklog;
}
