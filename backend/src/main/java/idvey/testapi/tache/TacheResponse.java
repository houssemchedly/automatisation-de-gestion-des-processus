package idvey.testapi.tache;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TacheResponse {

    private Integer id;
    private String titre;
    private String description;
    private Double estimation;
    private tachestatut statut;
    private String assigneANom;
    private Integer assigneAId;
    private String userStoryTitre;
    private Integer userStoryId;
    private BlocageInfo blocage;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BlocageInfo {
        private Integer id;
        private String titre;
        private String statut;
    }
}
