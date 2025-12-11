package idvey.testapi.sprint;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class SprintRequest {


    @NotEmpty(message = "Le nom du sprint est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
    private String nom;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotNull(message = "L'ID du projet est obligatoire")
    private Integer projetId;

    @Size(max = 500, message = "L'objectif ne peut pas dépasser 500 caractères")
    private String objectif;

    private sprintstatut statut;

    private Integer sprintBacklogId;
}
