package idvey.testapi.productBacklog;

import org.springframework.stereotype.Service;

@Service
public class BacklogItemMapper {

    public BacklogItem toBacklogItem(BacklogItemRequest request) {
        return BacklogItem.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .priorite(request.getPriorite())
                .points(request.getPoints())
                .statut(request.getStatut() != null ? request.getStatut() : itemstatut.A_FAIRE)
                .productBacklog(ProductBacklog.builder()
                        .id(request.getProductBacklogId())
                        .build())
                .build();
    }

    public BacklogItemResponse toBacklogItemResponse(BacklogItem backlogItem) {
        return BacklogItemResponse.builder()
                .id(backlogItem.getId())
                .titre(backlogItem.getTitre())
                .description(backlogItem.getDescription())
                .priorite(backlogItem.getPriorite())
                .points(backlogItem.getPoints())
                .statut(backlogItem.getStatut())
                .productBacklogId(backlogItem.getProductBacklog().getId())
                .projetNom(backlogItem.getProductBacklog().getProjet() != null ?
                        backlogItem.getProductBacklog().getProjet().getNom() : null)
                .statutLabel(getStatutLabel(backlogItem.getStatut()))
                .isCompleted(backlogItem.getStatut() == itemstatut.TERMINE)
                .isInProgress(backlogItem.getStatut() == itemstatut.EN_COURS)
                .build();
    }

    public void updateBacklogItemFromRequest(BacklogItemRequest request, BacklogItem backlogItem) {
        backlogItem.setTitre(request.getTitre());
        backlogItem.setDescription(request.getDescription());
        backlogItem.setPriorite(request.getPriorite());
        backlogItem.setPoints(request.getPoints());

        if (request.getStatut() != null) {
            backlogItem.setStatut(request.getStatut());
        }

        if (request.getProductBacklogId() != null &&
                !request.getProductBacklogId().equals(backlogItem.getProductBacklog().getId())) {
            backlogItem.setProductBacklog(ProductBacklog.builder()
                    .id(request.getProductBacklogId())
                    .build());
        }
    }

    private String getStatutLabel(itemstatut statut) {
        return switch (statut) {
            case A_FAIRE -> "À faire";
            case EN_COURS -> "En cours";
            case TERMINE -> "Terminé";
        };
    }
}
