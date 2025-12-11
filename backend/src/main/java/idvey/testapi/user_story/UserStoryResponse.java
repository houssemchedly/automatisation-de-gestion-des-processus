package idvey.testapi.user_story;

import idvey.testapi.tache.TacheResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStoryResponse {

    private Integer id;
    private String titre;
    private String description;
    private Integer priorite;
    private Integer points;
    private storystatut statut;
    private Integer sprintBacklogId;
    private String sprintBacklogNom;
    private List<TacheResponse> taches;
    private int nombreTaches;
    private int nombreTachesTerminees;
    private double pourcentageCompletion;
}
