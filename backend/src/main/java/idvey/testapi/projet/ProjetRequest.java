package idvey.testapi.projet;

import idvey.testapi.user.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ProjetRequest(
        Integer id,
        @NotNull(message = "100")
        @NotEmpty(message = "100")
        String nom,
        User productOwner,
        @NotNull(message = "102")
        @NotEmpty(message = "102")
        String description,
        LocalDate dateDebut,
        boolean actif,
        User scrumMaster,
        List<User> equipe,
        Integer productOwnerId,
        Integer scrumMasterId,
        List<Integer> equipeIds
)
{
}
