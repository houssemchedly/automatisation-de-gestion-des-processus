package idvey.testapi.sprint;

import idvey.testapi.common.PageResponse;
import idvey.testapi.projet.ProjetRepository;
import idvey.testapi.sprint_backlog.SprintBacklog;
import idvey.testapi.sprint_backlog.SprintBacklogRepository;
import idvey.testapi.user.User;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjetRepository projetRepository;
    private final SprintBacklogRepository sprintBacklogRepository;
    private final SprintMapper sprintMapper;

    public Integer save(SprintRequest request) {
        // Validate dates
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }

        var projet = projetRepository.findById(request.getProjetId())
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + request.getProjetId()));

        var sprint = sprintMapper.toSprint(request);
        sprint.setProjet(projet);

        var savedSprint = sprintRepository.save(sprint);

        // Create the associated sprint backlog
        var sprintBacklog = SprintBacklog.builder()
                .sprints(List.of(savedSprint))
                .build();
        sprintBacklogRepository.save(sprintBacklog);

        return savedSprint.getId();
    }

    @Transactional(readOnly = true)
    public SprintResponse findById(Integer sprintId) {
        return sprintRepository.findById(sprintId)
                .map(sprintMapper::toSprintResponse)
                .orElseThrow(() -> new EntityNotFoundException("Sprint non trouvé avec l'ID: " + sprintId));
    }

    @Transactional(readOnly = true)
    public PageResponse<SprintResponse> findAllSprints(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDebut").descending());
        Page<Sprint> sprints = sprintRepository.findAll(pageable);
        List<SprintResponse> sprintResponses = sprints.stream()
                .map(sprintMapper::toSprintResponse)
                .toList();

        return new PageResponse<>(
                sprintResponses,
                sprints.getNumber(),
                sprints.getSize(),
                sprints.getTotalElements(),
                sprints.getTotalPages(),
                sprints.isFirst(),
                sprints.isLast()
        );
    }


    @Transactional(readOnly = true)
    public List<SprintResponse> findSprintsBySprintBacklog(Integer sprintBacklogId) {
        return sprintRepository.findBySprintBacklogId(sprintBacklogId)
                .stream()
                .map(sprintMapper::toSprintResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> findSprintsByProject(Integer projetId) {
        return sprintRepository.findByProjetId(projetId)
                .stream()
                .map(sprintMapper::toSprintResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<SprintResponse> findSprintsByProductBacklog(Integer productBacklogId) {
        return sprintRepository.findByProductBacklogId(productBacklogId)
                .stream()
                .map(sprintMapper::toSprintResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<SprintResponse> findSprintsByStatut(sprintstatut statut, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDebut").descending());
        Page<Sprint> sprints = sprintRepository.findByStatut(statut, pageable);
        List<SprintResponse> sprintResponses = sprints.stream()
                .map(sprintMapper::toSprintResponse)
                .toList();

        return new PageResponse<>(
                sprintResponses,
                sprints.getNumber(),
                sprints.getSize(),
                sprints.getTotalElements(),
                sprints.getTotalPages(),
                sprints.isFirst(),
                sprints.isLast()
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<SprintResponse> searchSprints(String nom, sprintstatut statut, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDebut").descending());
        Page<Sprint> sprints = sprintRepository.findWithFilters(nom, statut, pageable);
        List<SprintResponse> sprintResponses = sprints.stream()
                .map(sprintMapper::toSprintResponse)
                .toList();

        return new PageResponse<>(
                sprintResponses,
                sprints.getNumber(),
                sprints.getSize(),
                sprints.getTotalElements(),
                sprints.getTotalPages(),
                sprints.isFirst(),
                sprints.isLast()
        );
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> findActiveSprintsOnDate(LocalDate date) {
        return sprintRepository.findActiveSprintsOnDate(date)
                .stream()
                .map(sprintMapper::toSprintResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> findCurrentActiveSprints() {
        return findActiveSprintsOnDate(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public PageResponse<SprintResponse> findSprintsByScrumMaster(Integer scrumMasterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDebut").descending());
        Page<Sprint> sprints = sprintRepository.findByProjetScrumMasterId(scrumMasterId, pageable);
        List<SprintResponse> sprintResponses = sprints.stream()
                .map(sprintMapper::toSprintResponse)
                .toList();

        return new PageResponse<>(
                sprintResponses,
                sprints.getNumber(),
                sprints.getSize(),
                sprints.getTotalElements(),
                sprints.getTotalPages(),
                sprints.isFirst(),
                sprints.isLast()
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<SprintResponse> findSprintsByScrumMaster(Authentication connectedUser, int page, int size) {
        User user = ((User) connectedUser.getPrincipal());
        return findSprintsByScrumMaster(user.getId(), page, size);
    }

    public SprintResponse updateSprint(Integer sprintId, SprintRequest request) {
        var sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint non trouvé avec l'ID: " + sprintId));

        // Validate dates
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }

        // Check if we can modify dates (only if the sprint hasn't started)
        if (sprint.getStatut() == sprintstatut.EN_COURS || sprint.getStatut() == sprintstatut.TERMINE) {
            if (!request.getDateDebut().equals(sprint.getDateDebut()) ||
                    !request.getDateFin().equals(sprint.getDateFin())) {
                throw new IllegalArgumentException("Impossible de modifier les dates d'un sprint en cours ou terminé");
            }
        }

        // Update the project if changed
        if (!request.getProjetId().equals(sprint.getProjet().getId())) {
            var projet = projetRepository.findById(request.getProjetId())
                    .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + request.getProjetId()));
            sprint.setProjet(projet);
        }

        sprint.setNom(request.getNom());
        sprint.setDateDebut(request.getDateDebut());
        sprint.setDateFin(request.getDateFin());
        sprint.setObjectif(request.getObjectif());

        return sprintMapper.toSprintResponse(sprintRepository.save(sprint));
    }

    public SprintResponse changeSprintStatut(Integer sprintId, sprintstatut newStatut) {
        var sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint non trouvé avec l'ID: " + sprintId));

        // Validate status transition
        validateStatusTransition(sprint.getStatut(), newStatut);

        // Additional validations based on the new status
        if (newStatut == sprintstatut.EN_COURS) {
            // Check if sprint dates are valid for starting
            LocalDate now = LocalDate.now();
            if (sprint.getDateDebut().isAfter(now)) {
                throw new IllegalArgumentException("Impossible de démarrer un sprint avant sa date de début");
            }
            if (sprint.getDateFin().isBefore(now)) {
                throw new IllegalArgumentException("Impossible de démarrer un sprint dont la date de fin est dépassée");
            }
        }

        sprint.setStatut(newStatut);
        return sprintMapper.toSprintResponse(sprintRepository.save(sprint));
    }

    public void deleteSprint(Integer sprintId) {
        var sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint non trouvé avec l'ID: " + sprintId));

        // Check if the sprint can be deleted
        if (sprint.getStatut() == sprintstatut.EN_COURS) {
            throw new IllegalArgumentException("Impossible de supprimer un sprint en cours");
        }

        // Check if sprint has user stories
        if (sprint.getSprintBacklog() != null &&
                sprint.getSprintBacklog().getUserStories() != null &&
                !sprint.getSprintBacklog().getUserStories().isEmpty()) {
            throw new IllegalArgumentException("Impossible de supprimer un sprint contenant des user stories");
        }

        sprintRepository.delete(sprint);
    }

    private void validateStatusTransition(sprintstatut currentStatut, sprintstatut newStatut) {
        if (currentStatut == newStatut) {
            throw new IllegalArgumentException("Le sprint a déjà ce statut");
        }

        switch (currentStatut) {
            case A_FAIRE:
                if (newStatut != sprintstatut.EN_COURS) {
                    throw new IllegalArgumentException("Un sprint 'À faire' ne peut passer qu'en 'En cours'");
                }
                break;
            case EN_COURS:
                if (newStatut != sprintstatut.TERMINE && newStatut != sprintstatut.A_FAIRE) {
                    throw new IllegalArgumentException("Un sprint 'En cours' ne peut passer qu'en 'Terminé' ou revenir 'À faire'");
                }
                break;
            case TERMINE:
                if (newStatut != sprintstatut.EN_COURS) {
                    throw new IllegalArgumentException("Un sprint 'Terminé' ne peut que reprendre 'En cours'");
                }
                break;
        }
    }
}
