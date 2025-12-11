package idvey.testapi.user_story;

import idvey.testapi.common.PageResponse;
import idvey.testapi.sprint_backlog.SprintBacklog;
import idvey.testapi.sprint_backlog.SprintBacklogRepository;
import idvey.testapi.tache.tachestatut;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final SprintBacklogRepository sprintBacklogRepository;
    private final UserStoryMapper userStoryMapper;

    public Integer save(UserStoryRequest request) {
        UserStory userStory = userStoryMapper.toUserStory(request);

        // Associer au sprint backlog si spécifié
        if (request.getSprintBacklogId() != null) {
            SprintBacklog sprintBacklog = sprintBacklogRepository.findById(request.getSprintBacklogId())
                    .orElseThrow(() -> new EntityNotFoundException("Sprint Backlog non trouvé avec l'ID: " + request.getSprintBacklogId()));
            userStory.setSprintBacklog(sprintBacklog);
        }

        return userStoryRepository.save(userStory).getId();
    }

    public UserStoryResponse findById(Integer userStoryId) {
        return userStoryRepository.findById(userStoryId)
                .map(userStoryMapper::toUserStoryResponse)
                .orElseThrow(() -> new EntityNotFoundException("User Story non trouvée avec l'ID: " + userStoryId));
    }

    public PageResponse<UserStoryResponse> findAllUserStories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("priorite").ascending());
        Page<UserStory> userStories = userStoryRepository.findAll(pageable);
        List<UserStoryResponse> userStoryResponses = userStories.stream()
                .map(userStoryMapper::toUserStoryResponse)
                .toList();

        return new PageResponse<>(
                userStoryResponses,
                userStories.getNumber(),
                userStories.getSize(),
                userStories.getTotalElements(),
                userStories.getTotalPages(),
                userStories.isFirst(),
                userStories.isLast()
        );
    }

    public PageResponse<UserStoryResponse> findWithFilters(String titre, storystatut statut,
                                                           Integer priorite, Integer sprintBacklogId,
                                                           int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("priorite").ascending());
        Page<UserStory> userStories = userStoryRepository.findWithFilters(titre, statut, priorite, sprintBacklogId, pageable);
        List<UserStoryResponse> userStoryResponses = userStories.stream()
                .map(userStoryMapper::toUserStoryResponse)
                .toList();

        return new PageResponse<>(
                userStoryResponses,
                userStories.getNumber(),
                userStories.getSize(),
                userStories.getTotalElements(),
                userStories.getTotalPages(),
                userStories.isFirst(),
                userStories.isLast()
        );
    }

    public List<UserStoryResponse> findByStatut(storystatut statut) {
        return userStoryRepository.findByStatut(statut)
                .stream()
                .map(userStoryMapper::toUserStoryResponse)
                .toList();
    }

    public List<UserStoryResponse> findBySprintBacklog(Integer sprintBacklogId) {
        return userStoryRepository.findBySprintBacklogId(sprintBacklogId)
                .stream()
                .map(userStoryMapper::toUserStoryResponse)
                .toList();
    }

    public void updateUserStory(Integer userStoryId, UserStoryRequest request) {
        UserStory userStory = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new EntityNotFoundException("User Story non trouvée avec l'ID: " + userStoryId));

        userStory.setTitre(request.getTitre());
        userStory.setDescription(request.getDescription());
        userStory.setPriorite(request.getPriorite());
        userStory.setPoints(request.getPoints());

        // Mettre à jour le sprint backlog si spécifié
        if (request.getSprintBacklogId() != null) {
            SprintBacklog sprintBacklog = sprintBacklogRepository.findById(request.getSprintBacklogId())
                    .orElseThrow(() -> new EntityNotFoundException("Sprint Backlog non trouvé avec l'ID: " + request.getSprintBacklogId()));
            userStory.setSprintBacklog(sprintBacklog);
        }

        userStoryRepository.save(userStory);
    }

    public void updateStatut(Integer userStoryId, storystatut nouveauStatut) {
        UserStory userStory = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new EntityNotFoundException("User Story non trouvée avec l'ID: " + userStoryId));

        // Validation des transitions de statut
        validateStatutTransition(userStory.getStatut(), nouveauStatut);

        // Vérifier la cohérence avec les tâches
        if (nouveauStatut == storystatut.TERMINE) {
            boolean hasIncompleteTasks = userStory.getTaches() != null &&
                    userStory.getTaches().stream()
                            .anyMatch(tache -> tache.getStatut() != tachestatut.TERMINE);

            if (hasIncompleteTasks) {
                throw new IllegalStateException("Impossible de terminer la User Story : certaines tâches ne sont pas terminées");
            }
        }

        userStory.setStatut(nouveauStatut);
        userStoryRepository.save(userStory);
    }

    public void deleteUserStory(Integer userStoryId) {
        UserStory userStory = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new EntityNotFoundException("User Story non trouvée avec l'ID: " + userStoryId));

        // Vérifier s'il y a des tâches associées
        if (userStory.getTaches() != null && !userStory.getTaches().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer la User Story : des tâches y sont associées");
        }

        userStoryRepository.delete(userStory);
    }

    public List<UserStoryResponse> findAllOrderByPriorite() {
        return userStoryRepository.findAllOrderByPriorite()
                .stream()
                .map(userStoryMapper::toUserStoryResponse)
                .toList();
    }

    private void validateStatutTransition(storystatut currentStatut, storystatut newStatut) {
        if (currentStatut == newStatut) {
            return; // Pas de changement
        }

        // Définir les transitions valides
        boolean isValidTransition = switch (currentStatut) {
            case A_FAIRE -> newStatut == storystatut.EN_COURS;
            case EN_COURS -> newStatut == storystatut.TERMINE || newStatut == storystatut.A_FAIRE;
            case TERMINE -> newStatut == storystatut.EN_COURS; // Permettre la réouverture
        };

        if (!isValidTransition) {
            throw new IllegalStateException(
                    String.format("Transition de statut invalide de %s vers %s", currentStatut, newStatut)
            );
        }
    }
}
