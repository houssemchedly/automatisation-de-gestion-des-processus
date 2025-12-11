package idvey.testapi.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class UserRequest {

    @NotEmpty(message = "Nom is mandatory")
    private String nom;

    @NotEmpty(message = "Prenom is mandatory")
    private String prenom;

    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is mandatory")
    private String email;

    private String password; // Optional for updates

    private LocalDate dateNaissance;

    private boolean enabled = true;

    private boolean accountLocked = false;

    private List<Integer> roleIds; // List of role IDs to assign to user
}
