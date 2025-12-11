package idvey.testapi.productBacklog;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BacklogItemResponse {

    private Integer id;
    private String titre;
    private String description;
    private Integer priorite;
    private Integer points;
    private itemstatut statut;
    private Integer productBacklogId;
    private String productBacklogTitre;
    private String projetNom;
    private  itemType type;
    // Additional computed fields
    private String statutLabel;
    private boolean isCompleted;
    private boolean isInProgress;
}
