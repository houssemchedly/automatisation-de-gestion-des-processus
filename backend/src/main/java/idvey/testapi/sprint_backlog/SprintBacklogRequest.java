package idvey.testapi.sprint_backlog;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SprintBacklogRequest {

    @NotNull(message = "Sprint ID is required")
    private Integer sprintId;

    private Integer productBacklogId;

    private List<Integer> userStoryIds;
}
