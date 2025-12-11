package idvey.testapi.meet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class MeetingRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotNull(message = "Le type de réunion est obligatoire")
    private meetype type;

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalDateTime debut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalDateTime fin;

    // Online meeting specific fields
    private String meetingUrl;
    private String meetingPassword;

    @NotNull(message = "Le projet est obligatoire")
    private Integer projetId;

    private List<Integer> participantIds;
}
