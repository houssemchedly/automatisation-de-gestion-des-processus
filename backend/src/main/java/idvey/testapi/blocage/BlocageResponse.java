package idvey.testapi.blocage;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlocageResponse {

    private Integer id;
    private String titre;
    private String description;
    private LocalDate dateSignalement;
    private LocalDate dateResolution;
    private blocstatut statut;
    private Integer priorite;
    private Integer tacheId;
    private String tacheTitre;
    private String commentaires;
    private boolean resolu;
}
