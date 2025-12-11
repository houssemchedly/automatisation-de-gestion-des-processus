package idvey.testapi.tache;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TacheRequest {

    @NotEmpty(message = "Le titre est obligatoire")
    @NotBlank(message = "Le titre ne peut pas être vide")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "L'estimation doit être positive")
    @DecimalMax(value = "999.99", message = "L'estimation ne peut pas dépasser 999.99")
    private Double estimation;

    private tachestatut statut;

    private Integer assigneAId; // User ID

    @NotNull(message = "L'ID de l'user story est obligatoire")
    private Integer userStoryId;
}

