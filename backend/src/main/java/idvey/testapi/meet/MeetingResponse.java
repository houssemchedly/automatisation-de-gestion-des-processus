package idvey.testapi.meet;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class MeetingResponse {

    private Integer id;
    private String titre;
    private String description;
    private meetype type;
    private LocalDate date;
    private LocalDateTime debut;
    private LocalDateTime fin;

    // Online meeting specific fields
    private String meetingUrl;
    private String meetingPassword;

    private Integer dureeMinutes;
    private boolean isUpcoming;
    private boolean isOngoing;
    private boolean isPast;

    // Project information
    private Integer projetId;
    private String projetNom;

    // the Participants information
    private List<ParticipantResponse> participants;
    private Integer nombreParticipants;

    @Getter
    @Setter
    @Builder
    public static class ParticipantResponse {
        private Integer id;
        private String prenom;
        private String nom;
        private String email;
    }
}
