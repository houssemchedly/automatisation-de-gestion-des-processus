package idvey.testapi.projet;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class ProjetMapper {
    public Projet toProjet(ProjetRequest request) {
        return Projet.builder()
                .id(request.id())
                .nom(request.nom())
               // .productOwner(request.productOwner())
                .description(request.description())
                .dateDebut(request.dateDebut())
                .actif(false)
                .build();
    }

    public ProjetResponse toProjetResponse(Projet projet) {
        return  ProjetResponse.builder()
                .id(projet.getId())
                .nom(projet.getNom())
                .description(projet.getDescription())
                //.productOwner(projet.getProductOwner().fullName())
                //.scrumMaster(projet.getScrumMaster().fullName())
                //.users(projet.getUsers())
                .statut(projet.getStatut())
                .actif(projet.isActif())
                .dateDebut(projet.getDateDebut())
                .productBacklog(projet.getProductBacklog())
                .build();

    }
}
