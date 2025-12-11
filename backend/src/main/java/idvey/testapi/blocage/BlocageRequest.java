package idvey.testapi.blocage;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class BlocageRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private LocalDate dateSignalement;

    private LocalDate dateResolution;

    @NotNull(message = "Le statut est obligatoire")
    private blocstatut statut;

    @NotNull(message = "La priorité est obligatoire")
    @Min(value = 1, message = "La priorité doit être comprise entre 1 et 5")
    @Max(value = 5, message = "La priorité doit être comprise entre 1 et 5")
    private Integer priorite;

    private Integer tacheId;

    private String commentaires;
}
