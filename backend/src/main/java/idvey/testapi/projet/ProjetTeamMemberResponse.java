package idvey.testapi.projet;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjetTeamMemberResponse {
    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private boolean accountLocked;
    private boolean enabled;
}
