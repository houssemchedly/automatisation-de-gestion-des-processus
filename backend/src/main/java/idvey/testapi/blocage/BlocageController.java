package idvey.testapi.blocage;


import idvey.testapi.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("blocages")
@RequiredArgsConstructor
@Tag(name = "Blocage")
public class BlocageController {

    private final BlocageService blocageService;

    @PostMapping
    @Operation(summary = "Créer un nouveau blocage")
    public ResponseEntity<Integer> createBlocage(
            @Valid @RequestBody BlocageRequest request
    ) {
        return ResponseEntity.ok(blocageService.createBlocage(request));
    }

    @GetMapping("/{blocage-id}")
    @Operation(summary = "Récupérer un blocage par son ID")
    public ResponseEntity<BlocageResponse> getBlocageById(
            @PathVariable("blocage-id") Integer blocageId
    ) {
        return ResponseEntity.ok(blocageService.findBlocageById(blocageId));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les blocages avec pagination")
    public ResponseEntity<PageResponse<BlocageResponse>> getAllBlocages(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(blocageService.findAllBlocages(page, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des blocages avec filtres")
    public ResponseEntity<PageResponse<BlocageResponse>> searchBlocages(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "titre", required = false) String titre,
            @RequestParam(name = "statut", required = false) blocstatut statut,
            @RequestParam(name = "priorite", required = false) Integer priorite
    ) {
        return ResponseEntity.ok(blocageService.findBlocagesWithFilters(page, size, titre, statut, priorite));
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les blocages par statut")
    public ResponseEntity<PageResponse<BlocageResponse>> getBlocagesByStatut(
            @PathVariable blocstatut statut,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(blocageService.findBlocagesByStatut(page, size, statut));
    }

    @PutMapping("/{blocage-id}")
    @Operation(summary = "Mettre à jour un blocage")
    public ResponseEntity<BlocageResponse> updateBlocage(
            @PathVariable("blocage-id") Integer blocageId,
            @Valid @RequestBody BlocageRequest request
    ) {
        return ResponseEntity.ok(blocageService.updateBlocage(blocageId, request));
    }

    @PatchMapping("/{blocage-id}/statut")
    @Operation(summary = "Changer le statut d'un blocage")
    public ResponseEntity<BlocageResponse> changeStatut(
            @PathVariable("blocage-id") Integer blocageId,
            @RequestParam blocstatut statut
    ) {
        return ResponseEntity.ok(blocageService.changeStatut(blocageId, statut));
    }


    @DeleteMapping("/{blocage-id}")
    @Operation(summary = "Supprimer un blocage")
    public ResponseEntity<Void> deleteBlocage(
            @PathVariable("blocage-id") Integer blocageId
    ) {
        blocageService.deleteBlocage(blocageId);
        return ResponseEntity.noContent().build();
    }
}
