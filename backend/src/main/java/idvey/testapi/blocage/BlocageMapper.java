package idvey.testapi.blocage;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlocageMapper {

    public Blocage toBlocage(BlocageRequest request) {
        return Blocage.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .dateSignalement(request.getDateSignalement() != null ?
                        request.getDateSignalement() : java.time.LocalDate.now())
                .dateResolution(request.getDateResolution())
                .statut(request.getStatut())
                .priorite(request.getPriorite())
                .commentaires(request.getCommentaires())
                .build();
    }

    public BlocageResponse toBlocageResponse(Blocage blocage) {
        return BlocageResponse.builder()
                .id(blocage.getId())
                .titre(blocage.getTitre())
                .description(blocage.getDescription())
                .dateSignalement(blocage.getDateSignalement())
                .dateResolution(blocage.getDateResolution())
                .statut(blocage.getStatut())
                .priorite(blocage.getPriorite())
                .tacheId(blocage.getTache() != null ? blocage.getTache().getId() : null)
                .tacheTitre(blocage.getTache() != null ? blocage.getTache().getTitre() : null)
                .commentaires(blocage.getCommentaires())
                .resolu(blocage.getStatut() == blocstatut.RESOLU)
                .build();
    }

    public void updateBlocageFromRequest(BlocageRequest request, Blocage blocage) {
        blocage.setTitre(request.getTitre());
        blocage.setDescription(request.getDescription());
        blocage.setDateResolution(request.getDateResolution());
        blocage.setStatut(request.getStatut());
        blocage.setPriorite(request.getPriorite());
        blocage.setCommentaires(request.getCommentaires());

        // Si le statut devient RESOLU et qu'il n'y a pas de date de résolution, on l'ajoute
        if (request.getStatut() == blocstatut.RESOLU && blocage.getDateResolution() == null) {
            blocage.setDateResolution(java.time.LocalDate.now());
        }
    }
}
