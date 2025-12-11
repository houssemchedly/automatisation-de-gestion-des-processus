package idvey.testapi.tache;

import org.springframework.stereotype.Service;

@Service
public class TacheMapper {

    public Tache toTache(TacheRequest request) {
        return Tache.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .estimation(request.getEstimation())
                .statut(request.getStatut() != null ? request.getStatut() : tachestatut.A_FAIRE)
                .build();
    }

    public TacheResponse toTacheResponse(Tache tache) {
        TacheResponse.TacheResponseBuilder responseBuilder = TacheResponse.builder()
                .id(tache.getId())
                .titre(tache.getTitre())
                .description(tache.getDescription())
                .estimation(tache.getEstimation())
                .statut(tache.getStatut());

        // User information
        if (tache.getUser() != null) {
            responseBuilder.assigneAId(tache.getUser().getId())
                    .assigneANom(tache.getUser().getPrenom() + " " + tache.getUser().getNom());
        }

        // UserStory information
        if (tache.getUserStory() != null) {
            responseBuilder.userStoryId(tache.getUserStory().getId())
                    .userStoryTitre(tache.getUserStory().getTitre());
        }

        // Blocage information
        if (tache.getBlocage() != null) {
            responseBuilder.blocage(TacheResponse.BlocageInfo.builder()
                    .id(tache.getBlocage().getId())
                    .titre(tache.getBlocage().getTitre())
                    .statut(tache.getBlocage().getStatut().toString())
                    .build());
        }

        return responseBuilder.build();
    }

    public void updateTacheFromRequest(TacheRequest request, Tache tache) {
        if (request.getTitre() != null) {
            tache.setTitre(request.getTitre());
        }
        if (request.getDescription() != null) {
            tache.setDescription(request.getDescription());
        }
        if (request.getEstimation() != null) {
            tache.setEstimation(request.getEstimation());
        }
        if (request.getStatut() != null) {
            tache.setStatut(request.getStatut());
        }
    }
}
