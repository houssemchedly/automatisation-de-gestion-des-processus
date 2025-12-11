package idvey.testapi.tache;

import idvey.testapi.common.PageResponse;
import idvey.testapi.exception.OperationNotPermittedException;
import idvey.testapi.user.User;
import idvey.testapi.user.UserRepository;
import idvey.testapi.user_story.UserStory;
import idvey.testapi.user_story.UserStoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class TacheService {

    private final TacheRepository tacheRepository;
    private final UserRepository userRepository;
    private final UserStoryRepository userStoryRepository;
    private final TacheMapper tacheMapper;

    public Integer save(TacheRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());

        // Verify UserStory exists
        UserStory userStory = userStoryRepository.findById(request.getUserStoryId())
                .orElseThrow(() -> new EntityNotFoundException("User Story non trouvée avec l'ID: " + request.getUserStoryId()));

        Tache tache = tacheMapper.toTache(request);
        tache.setUserStory(userStory);

        // Assign user if provided
        if (request.getAssigneAId() != null) {
            User assignedUser = userRepository.findById(request.getAssigneAId())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + request.getAssigneAId()));
            tache.setUser(assignedUser);
        }

        return tacheRepository.save(tache).getId();
    }

    public TacheResponse findById(Integer tacheId) {
        return tacheRepository.findById(tacheId)
                .map(tacheMapper::toTacheResponse)
                .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + tacheId));
    }

    public PageResponse<TacheResponse> findAllTaches(int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Tache> taches = tacheRepository.findAll(pageable);
        List<TacheResponse> tacheResponses = taches.stream()
                .map(tacheMapper::toTacheResponse)
                .toList();

        return new PageResponse<>(
                tacheResponses,
                taches.getNumber(),
                taches.getSize(),
                taches.getTotalElements(),
                taches.getTotalPages(),
                taches.isFirst(),
                taches.isLast()
        );
    }

    public PageResponse<TacheResponse> findTachesWithFilters(String titre, tachestatut statut,
                                                             Integer userId, Integer userStoryId,
                                                             int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Tache> taches = tacheRepository.findWithFilters(titre, statut, userId, userStoryId, pageable);
        List<TacheResponse> tacheResponses = taches.stream()
                .map(tacheMapper::toTacheResponse)
                .toList();

        return new PageResponse<>(
                tacheResponses,
                taches.getNumber(),
                taches.getSize(),
                taches.getTotalElements(),
                taches.getTotalPages(),
                taches.isFirst(),
                taches.isLast()
        );
    }

    public PageResponse<TacheResponse> findTachesByStatut(tachestatut statut, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Tache> taches = tacheRepository.findByStatut(statut, pageable);
        List<TacheResponse> tacheResponses = taches.stream()
                .map(tacheMapper::toTacheResponse)
                .toList();

        return new PageResponse<>(
                tacheResponses,
                taches.getNumber(),
                taches.getSize(),
                taches.getTotalElements(),
                taches.getTotalPages(),
                taches.isFirst(),
                taches.isLast()
        );
    }

    public PageResponse<TacheResponse> findMyTaches(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Tache> taches = tacheRepository.findByUserId(user.getId(), pageable);
        List<TacheResponse> tacheResponses = taches.stream()
                .map(tacheMapper::toTacheResponse)
                .toList();

        return new PageResponse<>(
                tacheResponses,
                taches.getNumber(),
                taches.getSize(),
                taches.getTotalElements(),
                taches.getTotalPages(),
                taches.isFirst(),
                taches.isLast()
        );
    }

    public Integer updateTache(Integer tacheId, TacheRequest request, Authentication connectedUser) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + tacheId));

        tacheMapper.updateTacheFromRequest(request, tache);

        // Update UserStory if provided
        if (request.getUserStoryId() != null && !Objects.equals(tache.getUserStory().getId(), request.getUserStoryId())) {
            UserStory userStory = userStoryRepository.findById(request.getUserStoryId())
                    .orElseThrow(() -> new EntityNotFoundException("User Story non trouvée avec l'ID: " + request.getUserStoryId()));
            tache.setUserStory(userStory);
        }

        // Update assigned user if provided
        if (request.getAssigneAId() != null) {
            if (request.getAssigneAId() == 0) {
                // Unassign user
                tache.setUser(null);
            } else if (tache.getUser() == null || !Objects.equals(tache.getUser().getId(), request.getAssigneAId())) {
                User assignedUser = userRepository.findById(request.getAssigneAId())
                        .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + request.getAssigneAId()));
                tache.setUser(assignedUser);
            }
        }

        return tacheRepository.save(tache).getId();
    }

    public Integer updateTacheStatut(Integer tacheId, tachestatut newStatut, Authentication connectedUser) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + tacheId));

        User user = ((User) connectedUser.getPrincipal());

        // Check if the user can change status (either assigned to the task or has admin role)
        if (tache.getUser() != null && !Objects.equals(tache.getUser().getId(), user.getId())) {
            boolean isAdmin = user.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));
            if (!isAdmin) {
                throw new OperationNotPermittedException("Vous ne pouvez modifier le statut que de vos propres tâches");
            }
        }

        tache.setStatut(newStatut);
        return tacheRepository.save(tache).getId();
    }

    public Integer assignTache(Integer tacheId, Integer userId, Authentication connectedUser) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + tacheId));

        if (userId == null || userId == 0) {
            // Unassign task
            tache.setUser(null);
        } else {
            User assignedUser = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
            tache.setUser(assignedUser);
        }

        return tacheRepository.save(tache).getId();
    }

    public void deleteTache(Integer tacheId, Authentication connectedUser) {
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'ID: " + tacheId));

        // Check if the task has a blocage
        if (tache.getBlocage() != null) {
            throw new OperationNotPermittedException("Impossible de supprimer une tâche qui a un blocage associé");
        }

        tacheRepository.delete(tache);
    }
}

