package idvey.testapi.productBacklog;

import idvey.testapi.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("backlog-items")
@RequiredArgsConstructor
@Tag(name = "BacklogItem")
public class BacklogItemController {

    private final BacklogItemService backlogItemService;

    @PostMapping
    public ResponseEntity<Integer> createBacklogItem(
            @Valid @RequestBody BacklogItemRequest request
    ) {
        return ResponseEntity.ok(backlogItemService.createBacklogItem(request));
    }

    @GetMapping("/{backlog-item-id}")
    public ResponseEntity<BacklogItemResponse> findBacklogItemById(
            @PathVariable("backlog-item-id") Integer backlogItemId
    ) {
        return ResponseEntity.ok(backlogItemService.findById(backlogItemId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BacklogItemResponse>> findAllBacklogItems(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(backlogItemService.findAllBacklogItems(page, size));
    }

    @GetMapping("/product-backlog/{product-backlog-id}")
    public ResponseEntity<PageResponse<BacklogItemResponse>> findByProductBacklog(
            @PathVariable("product-backlog-id") Integer productBacklogId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(backlogItemService.findByProductBacklog(productBacklogId, page, size));
    }

    @GetMapping("/by-project/{project-id}")
    @PreAuthorize("hasAnyRole('PRODUCT_OWNER', 'SCRUM_MASTER', 'ADMIN')")
    public ResponseEntity<PageResponse<BacklogItemResponse>> findByProjectId(
            @PathVariable("project-id") Integer projectId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(backlogItemService.findByProjectId(projectId, page, size, connectedUser));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<PageResponse<BacklogItemResponse>> findByStatut(
            @PathVariable itemstatut statut,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(backlogItemService.findByStatut(statut, page, size));
    }

    @GetMapping("/priorite")
    public ResponseEntity<List<BacklogItemResponse>> findByPriorityOrder(
            @RequestParam("product-backlog-id") Integer productBacklogId,
            @RequestParam(name = "ascending", defaultValue = "true") boolean ascending
    ) {
        return ResponseEntity.ok(backlogItemService.findByPriorityOrder(productBacklogId, ascending));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<BacklogItemResponse>> searchBacklogItems(
            @RequestParam("q") String searchTerm,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(backlogItemService.searchBacklogItems(searchTerm, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<BacklogItemResponse>> findWithFilters(
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) itemstatut statut,
            @RequestParam(name = "product-backlog-id", required = false) Integer productBacklogId,
            @RequestParam(name = "min-priorite", required = false) Integer minPriorite,
            @RequestParam(name = "max-priorite", required = false) Integer maxPriorite,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(backlogItemService.findWithFilters(
                titre, statut, productBacklogId, minPriorite, maxPriorite, page, size));
    }

    @PutMapping("/{backlog-item-id}")

    public ResponseEntity<BacklogItemResponse> updateBacklogItem(
            @PathVariable("backlog-item-id") Integer backlogItemId,
            @Valid @RequestBody BacklogItemRequest request
    ) {
        return ResponseEntity.ok(backlogItemService.updateBacklogItem(backlogItemId, request));
    }

    @PatchMapping("/{backlog-item-id}/statut")

    public ResponseEntity<BacklogItemResponse> changeStatut(
            @PathVariable("backlog-item-id") Integer backlogItemId,
            @RequestParam itemstatut statut
    ) {
        return ResponseEntity.ok(backlogItemService.changeStatut(backlogItemId, statut));
    }

    @PatchMapping("/{backlog-item-id}/priorite")

    public ResponseEntity<BacklogItemResponse> updatePriorite(
            @PathVariable("backlog-item-id") Integer backlogItemId,
            @RequestParam Integer priorite
    ) {
        return ResponseEntity.ok(backlogItemService.updatePriorite(backlogItemId, priorite));
    }

    @DeleteMapping("/{backlog-item-id}")

    public ResponseEntity<?> deleteBacklogItem(
            @PathVariable("backlog-item-id") Integer backlogItemId
    ) {
        backlogItemService.deleteBacklogItem(backlogItemId);
        return ResponseEntity.noContent().build();
    }
}
