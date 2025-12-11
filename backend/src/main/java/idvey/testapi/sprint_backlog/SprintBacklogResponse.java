package idvey.testapi.sprint_backlog;

import idvey.testapi.productBacklog.ProductBacklogResponse;
import idvey.testapi.sprint.SprintResponse;
import idvey.testapi.user_story.UserStoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprintBacklogResponse {

    private Integer id;
    private ProductBacklogResponse productBacklog;
    private List<SprintResponse> sprints;
    private List<UserStoryResponse> userStories;

    private Long totalUserStories;
    private Long completedUserStories;
    private Double completionPercentage;
    private Integer totalStoryPoints;
    private Integer completedStoryPoints;
    private Double storyPointsCompletionPercentage;
}
