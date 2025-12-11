package idvey.testapi.projet;

import idvey.testapi.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("projets")
@RequiredArgsConstructor
@Tag(name = "projet")
public class ProjetController {

    private final ProjetService service;

    @PostMapping
    public ResponseEntity<Integer> saveProjet (
            @Valid @RequestBody ProjetRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.save(request, connectedUser));
    }


    @GetMapping("/{projet-id}")
    public ResponseEntity<ProjetResponse> findProjetById(
            @PathVariable("projet-id") Integer projetId
    ){
        return ResponseEntity.ok(service.findById(projetId));
    }


    @GetMapping
    public ResponseEntity<PageResponse<ProjetResponse>> findAllProjets(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllProjets(page, size, connectedUser));
    }


    @GetMapping("/owner")
    public ResponseEntity<PageResponse<ProjetResponse>> findAllProjetsByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllProjetsByOwner(page, size, connectedUser));

    }

    @GetMapping("/scrum-master")
    public ResponseEntity<PageResponse<ProjetResponse>> findAllProjetsByScrumMaster(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllProjetsByScrumMaster(page, size, connectedUser));
    }

    @GetMapping("/scrum-master/{scrum-master-id}")
    public ResponseEntity<PageResponse<ProjetResponse>> findAllProjetsByScrumMasterId(
            @PathVariable("scrum-master-id") Integer scrumMasterId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ){
        return ResponseEntity.ok(service.findAllProjetsByScrumMasterId(scrumMasterId, page, size));
    }

    @PostMapping("/{projet-id}/team/add")
    public ResponseEntity<String> addTeamMembers(
            @PathVariable("projet-id") Integer projetId,
            @RequestBody List<Integer> userIds,
            Authentication connectedUser
    ){
        service.addTeamMembers(projetId, userIds, connectedUser);
        return ResponseEntity.ok("Team members added successfully");
    }

    @DeleteMapping("/{projet-id}/team/remove")
    public ResponseEntity<String> removeTeamMembers(
            @PathVariable("projet-id") Integer projetId,
            @RequestBody List<Integer> userIds,
            Authentication connectedUser
    ){
        service.removeTeamMembers(projetId, userIds, connectedUser);
        return ResponseEntity.ok("Team members removed successfully");
    }

    @GetMapping("/{projet-id}/team")
    public ResponseEntity<List<ProjetTeamMemberResponse>> getTeamMembers(
            @PathVariable("projet-id") Integer projetId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.getTeamMembers(projetId, connectedUser));
    }

    @PatchMapping("/actif/{projet-id}")
    public ResponseEntity<Integer> updateActifStatus(
            @PathVariable("projet-id") Integer projetId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.updateActifStatus(projetId, connectedUser));
    }

    @PutMapping("/update/{projet_id}")
    public ResponseEntity<ProjetResponse> updateProjet (
            @PathVariable("projet_id") Integer projetId,
            @Valid @RequestBody ProjetRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.updateProjet(projetId, request, connectedUser));
    }


    @DeleteMapping("/{projet-id}")
    public ResponseEntity<Void> deleteProjet(
            @PathVariable("projet-id") Integer projetId,
            Authentication connectedUser
    ){
        service.deleteProjet(projetId, connectedUser);
        return ResponseEntity.noContent().build();
    }
}
