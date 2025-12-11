package idvey.testapi.productBacklog;


import idvey.testapi.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("product-backlogs")
@RequiredArgsConstructor
@Tag(name = "ProductBacklog")
public class ProductBacklogController {

    private final ProductBacklogService service;

    @PostMapping
    public ResponseEntity<Integer> createProductBacklog(
            @Valid @RequestBody ProductBacklogRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @GetMapping("/{backlog-id}")
    public ResponseEntity<ProductBacklogResponse> findProductBacklogById(
            @PathVariable("backlog-id") Integer backlogId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findById(backlogId, connectedUser));
    }

    @GetMapping("/project/{project-id}")
    public ResponseEntity<ProductBacklogResponse> findProductBacklogByProjectId(
            @PathVariable("project-id") Integer projectId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findByProjectId(projectId, connectedUser));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductBacklogResponse>> findAllProductBacklogs(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllProductBacklogs(page, size, connectedUser));
    }

    @PutMapping("/{backlog-id}")
    public ResponseEntity<ProductBacklogResponse> updateProductBacklog(
            @PathVariable("backlog-id") Integer backlogId,
            @Valid @RequestBody ProductBacklogRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateProductBacklog(backlogId, request, connectedUser));
    }

    @DeleteMapping("/{backlog-id}")
    public ResponseEntity<Void> deleteProductBacklog(
            @PathVariable("backlog-id") Integer backlogId,
            Authentication connectedUser
    ) {
        service.deleteProductBacklog(backlogId, connectedUser);
        return ResponseEntity.noContent().build();
    }
}
