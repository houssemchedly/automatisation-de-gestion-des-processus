package idvey.testapi.sprint_backlog;

import idvey.testapi.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sprint-backlogs")
@RequiredArgsConstructor
@Tag(name = "Sprint Backlog", description = "Sprint Backlog management APIs")
public class SprintBacklogController {

    private final SprintBacklogService sprintBacklogService;

    @PostMapping
    @Operation(summary = "Create a new sprint backlog")
    public ResponseEntity<Integer> createSprintBacklog(
            @Valid @RequestBody SprintBacklogRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(sprintBacklogService.save(request));
    }

    @GetMapping("/{sprint-backlog-id}")
    @Operation(summary = "Get sprint backlog by ID")
    public ResponseEntity<SprintBacklogResponse> getSprintBacklogById(
            @Parameter(description = "Sprint Backlog ID")
            @PathVariable("sprint-backlog-id") Integer sprintBacklogId
    ) {
        return ResponseEntity.ok(sprintBacklogService.findById(sprintBacklogId));
    }



    @GetMapping
    @Operation(summary = "Get all sprint backlogs with pagination")
    public ResponseEntity<PageResponse<SprintBacklogResponse>> getAllSprintBacklogs(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @Parameter(description = "Page size")
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(sprintBacklogService.findAllSprintBacklogs(page, size));
    }

    @PostMapping("/{sprint-backlog-id}/user-stories/{user-story-id}")
    @Operation(summary = "Add user story to sprint backlog")
    public ResponseEntity<Void> addUserStoryToBacklog(
            @Parameter(description = "Sprint Backlog ID")
            @PathVariable("sprint-backlog-id") Integer sprintBacklogId,
            @Parameter(description = "User Story ID")
            @PathVariable("user-story-id") Integer userStoryId,
            Authentication connectedUser
    ) {
        sprintBacklogService.addUserStoryToBacklog(sprintBacklogId, userStoryId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sprint-backlog-id}/user-stories")
    @Operation(summary = "Add multiple user stories to sprint backlog")
    public ResponseEntity<Void> addUserStoriesToBacklog(
            @Parameter(description = "Sprint Backlog ID")
            @PathVariable("sprint-backlog-id") Integer sprintBacklogId,
            @RequestBody List<Integer> userStoryIds,
            Authentication connectedUser
    ) {
        sprintBacklogService.addUserStoriesToBacklog(sprintBacklogId, userStoryIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{sprint-backlog-id}/user-stories/{user-story-id}")
    @Operation(summary = "Remove user story from sprint backlog")
    public ResponseEntity<Void> removeUserStoryFromBacklog(
            @Parameter(description = "Sprint Backlog ID")
            @PathVariable("sprint-backlog-id") Integer sprintBacklogId,
            @Parameter(description = "User Story ID")
            @PathVariable("user-story-id") Integer userStoryId,
            Authentication connectedUser
    ) {
        sprintBacklogService.removeUserStoryFromBacklog(sprintBacklogId, userStoryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{sprint-backlog-id}")
    @Operation(summary = "Delete sprint backlog")
    public ResponseEntity<Void> deleteSprintBacklog(
            @Parameter(description = "Sprint Backlog ID")
            @PathVariable("sprint-backlog-id") Integer sprintBacklogId,
            Authentication connectedUser
    ) {
        sprintBacklogService.delete(sprintBacklogId);
        return ResponseEntity.ok().build();
    }
}
