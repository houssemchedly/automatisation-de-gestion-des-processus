package idvey.testapi.user_story;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserStoryRequest {

    @NotEmpty(message = "Le titre est obligatoire")
    @NotBlank(message = "Le titre ne peut pas être vide")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @NotNull(message = "La priorité est obligatoire")
    @Min(value = 1, message = "La priorité doit être au minimum 1")
    @Max(value = 10, message = "La priorité ne peut pas dépasser 10")
    private Integer priorite;

    @Min(value = 1, message = "Les points doivent être au minimum 1")
    @Max(value = 100, message = "Les points ne peuvent pas dépasser 100")
    private Integer points;

    private storystatut statut;

    private Integer sprintBacklogId;
}
