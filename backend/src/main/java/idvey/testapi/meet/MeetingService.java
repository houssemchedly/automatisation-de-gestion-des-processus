package idvey.testapi.meet;

import idvey.testapi.common.PageResponse;
import idvey.testapi.exception.OperationNotPermittedException;
import idvey.testapi.projet.ProjetRepository;
import idvey.testapi.user.User;
import idvey.testapi.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ProjetRepository projetRepository;
    private final UserRepository userRepository;
    private final MeetingMapper meetingMapper;

    public Integer save(MeetingRequest request, Authentication connectedUser) {
        // Validate project exists
        var projet = projetRepository.findById(request.getProjetId())
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + request.getProjetId()));

        // Validate time logic
        validateMeetingTimes(request.getDebut(), request.getFin());

        var meeting = meetingMapper.toMeeting(request);
        meeting.setProjet(projet);

        // Set participants if provided
        if (request.getParticipantIds() != null && !request.getParticipantIds().isEmpty()) {
            // Check for conflicts
            checkForConflicts(request.getParticipantIds(), request.getDebut(), request.getFin(), null);

            List<User> participants = userRepository.findAllById(request.getParticipantIds());
            if (participants.size() != request.getParticipantIds().size()) {
                throw new EntityNotFoundException("Un ou plusieurs participants n'ont pas été trouvés");
            }
            meeting.setParticipants(participants);
        }

        return meetingRepository.save(meeting).getId();
    }

    public MeetingResponse findById(Integer meetingId) {
        return meetingRepository.findById(meetingId)
                .map(meetingMapper::toMeetingResponse)
                .orElseThrow(() -> new EntityNotFoundException("Réunion non trouvée avec l'ID: " + meetingId));
    }

    public PageResponse<MeetingResponse> findAllMeetings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("debut").descending());
        Page<Meeting> meetings = meetingRepository.findAll(pageable);
        List<MeetingResponse> meetingResponses = meetings.stream()
                .map(meetingMapper::toMeetingResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                meetingResponses,
                meetings.getNumber(),
                meetings.getSize(),
                meetings.getTotalElements(),
                meetings.getTotalPages(),
                meetings.isFirst(),
                meetings.isLast()
        );
    }

    public PageResponse<MeetingResponse> findMeetingsByType(meetype type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("debut").descending());
        Page<Meeting> meetings = meetingRepository.findByType(type, pageable);
        List<MeetingResponse> meetingResponses = meetings.stream()
                .map(meetingMapper::toMeetingResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                meetingResponses,
                meetings.getNumber(),
                meetings.getSize(),
                meetings.getTotalElements(),
                meetings.getTotalPages(),
                meetings.isFirst(),
                meetings.isLast()
        );
    }

    public PageResponse<MeetingResponse> findMeetingsByProject(Integer projetId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("debut").descending());
        Page<Meeting> meetings = meetingRepository.findByProjetId(projetId, pageable);
        List<MeetingResponse> meetingResponses = meetings.stream()
                .map(meetingMapper::toMeetingResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                meetingResponses,
                meetings.getNumber(),
                meetings.getSize(),
                meetings.getTotalElements(),
                meetings.getTotalPages(),
                meetings.isFirst(),
                meetings.isLast()
        );
    }

    public PageResponse<MeetingResponse> findMyMeetings(Authentication connectedUser, int page, int size) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("debut").descending());
        Page<Meeting> meetings = meetingRepository.findByParticipantId(user.getId(), pageable);
        List<MeetingResponse> meetingResponses = meetings.stream()
                .map(meetingMapper::toMeetingResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                meetingResponses,
                meetings.getNumber(),
                meetings.getSize(),
                meetings.getTotalElements(),
                meetings.getTotalPages(),
                meetings.isFirst(),
                meetings.isLast()
        );
    }

    public List<MeetingResponse> findUpcomingMeetings() {
        List<Meeting> meetings = meetingRepository.findUpcomingMeetings(LocalDateTime.now());
        return meetings.stream()
                .map(meetingMapper::toMeetingResponse)
                .collect(Collectors.toList());
    }

    public List<MeetingResponse> findTodaysMeetings() {
        List<Meeting> meetings = meetingRepository.findTodaysMeetings(LocalDate.now());
        return meetings.stream()
                .map(meetingMapper::toMeetingResponse)
                .collect(Collectors.toList());
    }

    public Integer updateMeeting(Integer meetingId, MeetingRequest request) {
        var meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("Réunion non trouvée avec l'ID: " + meetingId));

        // Check if meeting is in the past
        if (meeting.getFin() != null && meeting.getFin().isBefore(LocalDateTime.now())) {
            throw new OperationNotPermittedException("Impossible de modifier une réunion passée");
        }

        // Validate time logic
        validateMeetingTimes(request.getDebut(), request.getFin());

        // Update meeting using mapper
        meetingMapper.updateMeetingFromRequest(meeting, request);

        // Update project if needed
        if (request.getProjetId() != null &&
                (meeting.getProjet() == null || !meeting.getProjet().getId().equals(request.getProjetId()))) {
            var projet = projetRepository.findById(request.getProjetId())
                    .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + request.getProjetId()));
            meeting.setProjet(projet);
        }

        // Update participants
        if (request.getParticipantIds() != null) {
            // Check for conflicts
            checkForConflicts(request.getParticipantIds(), request.getDebut(), request.getFin(), meetingId);

            List<User> participants = userRepository.findAllById(request.getParticipantIds());
            if (participants.size() != request.getParticipantIds().size()) {
                throw new EntityNotFoundException("Un ou plusieurs participants n'ont pas été trouvés");
            }
            meeting.setParticipants(participants);
        }

        return meetingRepository.save(meeting).getId();
    }

    public void addParticipant(Integer meetingId, Integer userId) {
        var meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("Réunion non trouvée avec l'ID: " + meetingId));

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));

        // Check if user is already a participant
        if (meeting.getParticipants().stream().anyMatch(p -> p.getId().equals(userId))) {
            throw new OperationNotPermittedException("L'utilisateur participe déjà à cette réunion");
        }

        // Check for conflicts
        if (meetingRepository.hasConflictingMeeting(userId, meeting.getDebut(), meeting.getFin(), meetingId)) {
            throw new OperationNotPermittedException("L'utilisateur a un conflit d'horaire avec une autre réunion");
        }

        meeting.getParticipants().add(user);
        meetingRepository.save(meeting);
    }

    public void removeParticipant(Integer meetingId, Integer userId) {
        var meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("Réunion non trouvée avec l'ID: " + meetingId));

        boolean removed = meeting.getParticipants().removeIf(participant -> participant.getId().equals(userId));

        if (!removed) {
            throw new EntityNotFoundException("Participant non trouvé dans cette réunion");
        }

        meetingRepository.save(meeting);
    }

    public void deleteMeeting(Integer meetingId) {
        var meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new EntityNotFoundException("Réunion non trouvée avec l'ID: " + meetingId));

        // Check if meeting is ongoing
        LocalDateTime now = LocalDateTime.now();
        if (meeting.getDebut() != null && meeting.getFin() != null &&
                meeting.getDebut().isBefore(now) && meeting.getFin().isAfter(now)) {
            throw new OperationNotPermittedException("Impossible de supprimer une réunion en cours");
        }

        meetingRepository.delete(meeting);
    }

    private void validateMeetingTimes(LocalDateTime debut, LocalDateTime fin) {
        if (debut.isAfter(fin)) {
            throw new OperationNotPermittedException("L'heure de début doit être antérieure à l'heure de fin");
        }

        if (debut.isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new OperationNotPermittedException("Impossible de programmer une réunion dans le passé");
        }
    }

    private void checkForConflicts(List<Integer> participantIds, LocalDateTime debut, LocalDateTime fin, Integer excludeMeetingId) {
        for (Integer participantId : participantIds) {
            if (meetingRepository.hasConflictingMeeting(participantId, debut, fin, excludeMeetingId)) {
                var user = userRepository.findById(participantId).orElse(null);
                String userName = user != null ? user.getPrenom() + " " + user.getNom() : "Utilisateur " + participantId;
                throw new OperationNotPermittedException("Conflit d'horaire détecté pour " + userName);
            }
        }
    }
}
