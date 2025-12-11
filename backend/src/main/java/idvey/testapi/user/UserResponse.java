package idvey.testapi.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class UserResponse {

    private Integer id;
    private String nom;
    private String prenom;
    private String email;
    private String fullName;
    private LocalDate dateNaissance;
    private boolean enabled;
    private boolean accountLocked;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private List<RoleResponse> roles;

    @Getter
    @Setter
    @Builder
    public static class RoleResponse {
        private Integer id;
        private String name;
        private LocalDateTime createdDate;
        private LocalDateTime lastModifiedDate;
    }
}
