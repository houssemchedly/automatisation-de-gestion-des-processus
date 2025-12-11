package idvey.testapi.sprint_backlog;

import idvey.testapi.productBacklog.ProductBacklogMapper;
import idvey.testapi.sprint.SprintMapper;
import idvey.testapi.user_story.UserStoryMapper;
import org.springframework.stereotype.Service;

@Service
public class SprintBacklogMapper {

    private final UserStoryMapper userStoryMapper;
    private final SprintMapper sprintMapper;
    private final ProductBacklogMapper productBacklogMapper;

    public SprintBacklogMapper(UserStoryMapper userStoryMapper, SprintMapper sprintMapper, ProductBacklogMapper productBacklogMapper) {
        this.userStoryMapper = userStoryMapper;
        this.sprintMapper = sprintMapper;
        this.productBacklogMapper = productBacklogMapper;
    }

    public SprintBacklogResponse toSprintBacklogResponse(SprintBacklog sprintBacklog) {
        return SprintBacklogResponse.builder()
                .id(sprintBacklog.getId())
                .productBacklog(sprintBacklog.getProductBacklog() != null ?
                        productBacklogMapper.toProductBacklogResponse(sprintBacklog.getProductBacklog()) : null)
                .sprints(sprintBacklog.getSprints() != null ?
                        sprintBacklog.getSprints().stream()
                                .map(sprintMapper::toSprintResponse)
                                .toList() : null)
                .userStories(sprintBacklog.getUserStories() != null ?
                        sprintBacklog.getUserStories().stream()
                                .map(userStoryMapper::toUserStoryResponse)
                                .toList() : null)
                .build();
    }

    public SprintBacklogResponse toSprintBacklogResponseWithMetrics(SprintBacklog sprintBacklog,
                                                                    Long totalUserStories,
                                                                    Long completedUserStories,
                                                                    Integer totalStoryPoints,
                                                                    Integer completedStoryPoints) {
        SprintBacklogResponse response = toSprintBacklogResponse(sprintBacklog);

        response.setTotalUserStories(totalUserStories);
        response.setCompletedUserStories(completedUserStories);
        response.setTotalStoryPoints(totalStoryPoints != null ? totalStoryPoints : 0);
        response.setCompletedStoryPoints(completedStoryPoints != null ? completedStoryPoints : 0);

        // Calculate completion percentages
        if (totalUserStories > 0) {
            response.setCompletionPercentage((double) completedUserStories / totalUserStories * 100);
        } else {
            response.setCompletionPercentage(0.0);
        }

        if (response.getTotalStoryPoints() > 0) {
            response.setStoryPointsCompletionPercentage(
                    (double) response.getCompletedStoryPoints() / response.getTotalStoryPoints() * 100);
        } else {
            response.setStoryPointsCompletionPercentage(0.0);
        }

        return response;
    }
}
