package idvey.testapi.sprint;

import idvey.testapi.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("sprints")
@RequiredArgsConstructor
@Tag(name = "Sprint", description = "API de gestion des sprints")
public class SprintController {

    private final SprintService sprintService;

    @PostMapping("/add")
    @Operation(summary = "Créer un nouveau sprint")
    public ResponseEntity<Integer> saveSprint(
            @Valid @RequestBody SprintRequest request
    ) {
        return ResponseEntity.ok(sprintService.save(request));
    }

    @GetMapping("/{sprint-id}")
    @Operation(summary = "Récupérer un sprint par son ID")
    public ResponseEntity<SprintResponse> findSprintById(
            @PathVariable("sprint-id") Integer sprintId
    ) {
        return ResponseEntity.ok(sprintService.findById(sprintId));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les sprints avec pagination")
    public ResponseEntity<PageResponse<SprintResponse>> findAllSprints(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(sprintService.findAllSprints(page, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des sprints avec filtres")
    public ResponseEntity<PageResponse<SprintResponse>> searchSprints(
            @RequestParam(name = "nom", required = false) String nom,
            @RequestParam(name = "statut", required = false) sprintstatut statut,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(sprintService.searchSprints(nom, statut, page, size));
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les sprints par statut")
    public ResponseEntity<PageResponse<SprintResponse>> findSprintsByStatut(
            @PathVariable sprintstatut statut,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(sprintService.findSprintsByStatut(statut, page, size));
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer les sprints actuellement actifs")
    public ResponseEntity<List<SprintResponse>> findCurrentActiveSprints() {
        return ResponseEntity.ok(sprintService.findCurrentActiveSprints());
    }

    @GetMapping("/active/{date}")
    @Operation(summary = "Récupérer les sprints actifs à une date donnée")
    public ResponseEntity<List<SprintResponse>> findActiveSprintsOnDate(
            @PathVariable @Parameter(description = "Date au format YYYY-MM-DD") LocalDate date
    ) {
        return ResponseEntity.ok(sprintService.findActiveSprintsOnDate(date));
    }

    @PutMapping("/{sprint-id}")
    @Operation(summary = "Mettre à jour un sprint")
    public ResponseEntity<SprintResponse> updateSprint(
            @PathVariable("sprint-id") Integer sprintId,
            @Valid @RequestBody SprintRequest request
    ) {
        return ResponseEntity.ok(sprintService.updateSprint(sprintId, request));
    }

    @PatchMapping("/{sprint-id}/statut")
    @Operation(summary = "Changer le statut d'un sprint")
    public ResponseEntity<SprintResponse> changeSprintStatut(
            @PathVariable("sprint-id") Integer sprintId,
            @RequestParam sprintstatut statut
    ) {
        return ResponseEntity.ok(sprintService.changeSprintStatut(sprintId, statut));
    }

    @DeleteMapping("/{sprint-id}")
    @Operation(summary = "Supprimer un sprint")
    public ResponseEntity<Void> deleteSprint(
            @PathVariable("sprint-id") Integer sprintId
    ) {
        sprintService.deleteSprint(sprintId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sprint-backlog/{sprint-backlog-id}")
    @Operation(summary = "Récupérer les sprints par ID de sprint backlog")
    public ResponseEntity<List<SprintResponse>> findSprintsBySprintBacklog(
            @PathVariable("sprint-backlog-id") Integer sprintBacklogId
    ) {
        return ResponseEntity.ok(sprintService.findSprintsBySprintBacklog(sprintBacklogId));
    }

    @GetMapping("/project/{project-id}")
    @Operation(summary = "Récupérer les sprints par ID de projet")
    public ResponseEntity<List<SprintResponse>> findSprintsByProject(
            @PathVariable("project-id") Integer projectId
    ) {
        return ResponseEntity.ok(sprintService.findSprintsByProject(projectId));
    }

    @GetMapping("/product-backlog/{product-backlog-id}")
    @Operation(summary = "Récupérer les sprints par ID de product backlog")
    public ResponseEntity<List<SprintResponse>> findSprintsByProductBacklog(
            @PathVariable("product-backlog-id") Integer productBacklogId
    ) {
        return ResponseEntity.ok(sprintService.findSprintsByProductBacklog(productBacklogId));
    }

    @GetMapping("/scrum-master/{scrum-master-id}")
    @Operation(summary = "Récupérer tous les sprints d'un Scrum Master")
    public ResponseEntity<PageResponse<SprintResponse>> findSprintsByScrumMaster(
            @PathVariable("scrum-master-id") Integer scrumMasterId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(sprintService.findSprintsByScrumMaster(scrumMasterId, page, size));
    }

    @GetMapping("/my-sprints")
    @Operation(summary = "Récupérer tous les sprints du Scrum Master connecté")
    public ResponseEntity<PageResponse<SprintResponse>> findMySprintsAsScrumMaster(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(sprintService.findSprintsByScrumMaster(connectedUser, page, size));
    }
}
