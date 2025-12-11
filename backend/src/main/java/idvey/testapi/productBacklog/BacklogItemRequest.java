package idvey.testapi.productBacklog;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BacklogItemRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 3, max = 100, message = "Le titre doit contenir entre 3 et 100 caractères")
    private String titre;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @NotNull(message = "La priorité est obligatoire")
    @Min(value = 1, message = "La priorité doit être au moins 1")
    @Max(value = 100, message = "La priorité ne peut pas dépasser 100")
    private Integer priorite;

    @Min(value = 1, message = "Les points doivent être au moins 1")
    @Max(value = 100, message = "Les points ne peuvent pas dépasser 100")
    private Integer points;

    private itemstatut statut;

    private itemType type;

    @NotNull(message = "L'ID du product backlog est obligatoire")
    private Integer productBacklogId;
}
