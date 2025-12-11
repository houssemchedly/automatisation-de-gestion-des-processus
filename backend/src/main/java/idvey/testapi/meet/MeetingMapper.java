package idvey.testapi.meet;

import idvey.testapi.projet.Projet;
import idvey.testapi.user.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingMapper {

    public Meeting toMeeting(MeetingRequest request) {
        Meeting meeting = new Meeting();
        updateMeetingFromRequest(meeting, request);

        // Initialize the participants list if needed
        if (meeting.getParticipants() == null) {
            meeting.setParticipants(new ArrayList<>());
        }

        return meeting;
    }

    /**
     * Updates a meeting entity with data from a request
     * This method avoids code duplication between create and update operations
     */
    public void updateMeetingFromRequest(Meeting meeting, MeetingRequest request) {
        meeting.setTitre(request.getTitre());
        meeting.setDescription(request.getDescription());
        meeting.setType(request.getType());
        meeting.setDate(request.getDate());
        meeting.setDebut(request.getDebut());
        meeting.setFin(request.getFin());
        meeting.setMeetingUrl(request.getMeetingUrl());
        meeting.setMeetingPassword(request.getMeetingPassword());

        // Set project reference
        if (request.getProjetId() != null) {
            Projet projet = new Projet();
            projet.setId(request.getProjetId());
            meeting.setProjet(projet);
        }
    }

    public MeetingResponse toMeetingResponse(Meeting meeting) {
        LocalDateTime now = LocalDateTime.now();

        return MeetingResponse.builder()
                .id(meeting.getId())
                .titre(meeting.getTitre())
                .description(meeting.getDescription())
                .type(meeting.getType())
                .date(meeting.getDate())
                .debut(meeting.getDebut())
                .fin(meeting.getFin())
                .meetingUrl(meeting.getMeetingUrl())
                .meetingPassword(meeting.getMeetingPassword())
                .dureeMinutes(calculateDurationInMinutes(meeting.getDebut(), meeting.getFin()))
                .isUpcoming(meeting.getDebut() != null && meeting.getDebut().isAfter(now))
                .isOngoing(meeting.getDebut() != null && meeting.getFin() != null &&
                        meeting.getDebut().isBefore(now) && meeting.getFin().isAfter(now))
                .isPast(meeting.getFin() != null && meeting.getFin().isBefore(now))
                .projetId(meeting.getProjet() != null ? meeting.getProjet().getId() : null)
                .projetNom(meeting.getProjet() != null ? meeting.getProjet().getNom() : null)
                .participants(toParticipantResponses(meeting.getParticipants()))
                .nombreParticipants(meeting.getParticipants() != null ? meeting.getParticipants().size() : 0)
                .build();
    }

    private List<MeetingResponse.ParticipantResponse> toParticipantResponses(List<User> participants) {
        if (participants == null) {
            return List.of();
        }

        return participants.stream()
                .map(user -> MeetingResponse.ParticipantResponse.builder()
                        .id(user.getId())
                        .prenom(user.getPrenom())
                        .nom(user.getNom())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    private Integer calculateDurationInMinutes(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null) {
            return 0;
        }
        return (int) Duration.between(debut, fin).toMinutes();
    }
}
