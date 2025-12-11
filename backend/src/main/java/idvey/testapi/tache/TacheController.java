package idvey.testapi.tache;


import idvey.testapi.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("taches")
@RequiredArgsConstructor
@Tag(name = "Tache")
public class TacheController {

    private final TacheService service;

    @PostMapping
    public ResponseEntity<Integer> saveTache(
            @Valid @RequestBody TacheRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @GetMapping("/{tache-id}")
    public ResponseEntity<TacheResponse> findTacheById(
            @PathVariable("tache-id") Integer tacheId
    ) {
        return ResponseEntity.ok(service.findById(tacheId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<TacheResponse>> findAllTaches(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllTaches(page, size, connectedUser));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<TacheResponse>> findTachesWithFilters(
            @RequestParam(name = "titre", required = false) String titre,
            @RequestParam(name = "statut", required = false) tachestatut statut,
            @RequestParam(name = "userId", required = false) Integer userId,
            @RequestParam(name = "userStoryId", required = false) Integer userStoryId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findTachesWithFilters(titre, statut, userId, userStoryId, page, size, connectedUser));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<PageResponse<TacheResponse>> findTachesByStatut(
            @PathVariable tachestatut statut,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findTachesByStatut(statut, page, size, connectedUser));
    }

    @GetMapping("/mes-taches")
    public ResponseEntity<PageResponse<TacheResponse>> findMyTaches(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findMyTaches(page, size, connectedUser));
    }

    @PutMapping("/{tache-id}")
    public ResponseEntity<Integer> updateTache(
            @PathVariable("tache-id") Integer tacheId,
            @Valid @RequestBody TacheRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.updateTache(tacheId, request, connectedUser));
    }

    @PatchMapping("/{tache-id}/statut")
    public ResponseEntity<Integer> updateTacheStatut(
            @PathVariable("tache-id") Integer tacheId,
            @RequestBody Map<String, String> statutRequest,
            Authentication connectedUser
    ) {
        tachestatut newStatut = tachestatut.valueOf(statutRequest.get("statut"));
        return ResponseEntity.ok(service.updateTacheStatut(tacheId, newStatut, connectedUser));
    }

    @PatchMapping("/{tache-id}/assign")
    public ResponseEntity<Integer> assignTache(
            @PathVariable("tache-id") Integer tacheId,
            @RequestBody Map<String, Integer> assignRequest,
            Authentication connectedUser
    ) {
        Integer userId = assignRequest.get("userId");
        return ResponseEntity.ok(service.assignTache(tacheId, userId, connectedUser));
    }

    @DeleteMapping("/{tache-id}")
    public ResponseEntity<?> deleteTache(
            @PathVariable("tache-id") Integer tacheId,
            Authentication connectedUser
    ) {
        service.deleteTache(tacheId, connectedUser);
        return ResponseEntity.noContent().build();
    }
}
