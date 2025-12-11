package idvey.testapi.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @NotEmpty(message = "Le nom est obligatoire")
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    @NotEmpty(message = "Le prenom est obligatoire")
    @NotBlank(message = "Le prenom est obligatoire")
    private String prenom;
    @Email(message = "L'adresse e-mail n'est pas valide")
    @NotEmpty(message = "L'email est obligatoire")
    @NotBlank(message = "L'email est obligatoire")
    private String email;
    @NotEmpty(message = "Le mdp est obligatoire")
    @NotBlank(message = "Le mdp est obligatoire")
    @Size(min = 8 , message = "le mdp doit contenir au moins 8 caractéres")
    private String password;
}