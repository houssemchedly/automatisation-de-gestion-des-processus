package idvey.testapi.user_story;

import idvey.testapi.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user-stories")
@RequiredArgsConstructor
@Tag(name = "User Story")
public class UserStoryController {

    private final UserStoryService userStoryService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle User Story")
    public ResponseEntity<Integer> saveUserStory(
            @Valid @RequestBody UserStoryRequest request
    ) {
        return ResponseEntity.ok(userStoryService.save(request));
    }

    @GetMapping("/{user-story-id}")
    @Operation(summary = "Récupérer une User Story par son ID")
    public ResponseEntity<UserStoryResponse> findUserStoryById(
            @PathVariable("user-story-id") Integer userStoryId
    ) {
        return ResponseEntity.ok(userStoryService.findById(userStoryId));
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les User Stories avec pagination")
    public ResponseEntity<PageResponse<UserStoryResponse>> findAllUserStories(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(userStoryService.findAllUserStories(page, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des User Stories avec filtres")
    public ResponseEntity<PageResponse<UserStoryResponse>> searchUserStories(
            @Parameter(description = "Titre de la User Story") @RequestParam(required = false) String titre,
            @Parameter(description = "Statut de la User Story") @RequestParam(required = false) storystatut statut,
            @Parameter(description = "Priorité de la User Story") @RequestParam(required = false) Integer priorite,
            @Parameter(description = "ID du Sprint Backlog") @RequestParam(required = false) Integer sprintBacklogId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(userStoryService.findWithFilters(titre, statut, priorite, sprintBacklogId, page, size));
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les User Stories par statut")
    public ResponseEntity<List<UserStoryResponse>> findUserStoriesByStatut(
            @PathVariable storystatut statut
    ) {
        return ResponseEntity.ok(userStoryService.findByStatut(statut));
    }

    @GetMapping("/sprint-backlog/{sprint-backlog-id}")
    @Operation(summary = "Récupérer les User Stories d'un Sprint Backlog")
    public ResponseEntity<List<UserStoryResponse>> findUserStoriesBySprintBacklog(
            @PathVariable("sprint-backlog-id") Integer sprintBacklogId
    ) {
        return ResponseEntity.ok(userStoryService.findBySprintBacklog(sprintBacklogId));
    }

    @GetMapping("/priorite")
    @Operation(summary = "Récupérer toutes les User Stories triées par priorité")
    public ResponseEntity<List<UserStoryResponse>> findAllUserStoriesOrderByPriorite() {
        return ResponseEntity.ok(userStoryService.findAllOrderByPriorite());
    }

    @PutMapping("/{user-story-id}")
    @Operation(summary = "Mettre à jour une User Story")
    public ResponseEntity<Void> updateUserStory(
            @PathVariable("user-story-id") Integer userStoryId,
            @Valid @RequestBody UserStoryRequest request
    ) {
        userStoryService.updateUserStory(userStoryId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{user-story-id}/statut")
    @Operation(summary = "Changer le statut d'une User Story")
    public ResponseEntity<Void> updateUserStoryStatut(
            @PathVariable("user-story-id") Integer userStoryId,
            @RequestParam storystatut statut
    ) {
        userStoryService.updateStatut(userStoryId, statut);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{user-story-id}")
    @Operation(summary = "Supprimer une User Story")
    public ResponseEntity<Void> deleteUserStory(
            @PathVariable("user-story-id") Integer userStoryId
    ) {
        userStoryService.deleteUserStory(userStoryId);
        return ResponseEntity.ok().build();
    }
}
