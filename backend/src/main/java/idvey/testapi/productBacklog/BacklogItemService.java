package idvey.testapi.productBacklog;

import idvey.testapi.common.PageResponse;
import idvey.testapi.exception.OperationNotPermittedException;
import idvey.testapi.projet.Projet;
import idvey.testapi.projet.ProjetRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BacklogItemService {

    private final BacklogItemRepository backlogItemRepository;
    private final BacklogItemMapper backlogItemMapper;
    private final ProductBacklogRepository productBacklogRepository;
    private final ProjetRepository projetRepository;

    public Integer createBacklogItem(BacklogItemRequest request) {
        // Verify product backlog exists
        var productBacklog = productBacklogRepository.findById(request.getProductBacklogId())
                .orElseThrow(() -> new EntityNotFoundException("Product Backlog non trouvé avec l'ID: " + request.getProductBacklogId()));

        // Check if the priority already exists and adjust if necessary
        if (backlogItemRepository.existsByProductBacklogIdAndPriorite(request.getProductBacklogId(), request.getPriorite())) {
            adjustPriorities(request.getProductBacklogId(), request.getPriorite());
        }

        var backlogItem = backlogItemMapper.toBacklogItem(request);
        backlogItem.setProductBacklog(productBacklog);

        return backlogItemRepository.save(backlogItem).getId();
    }

    public BacklogItemResponse findById(Integer id) {
        return backlogItemRepository.findById(id)
                .map(backlogItemMapper::toBacklogItemResponse)
                .orElseThrow(() -> new EntityNotFoundException("Backlog Item non trouvé avec l'ID: " + id));
    }

    public PageResponse<BacklogItemResponse> findAllBacklogItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("priorite").ascending());
        Page<BacklogItem> backlogItems = backlogItemRepository.findAll(pageable);
        List<BacklogItemResponse> backlogItemResponses = backlogItems.stream()
                .map(backlogItemMapper::toBacklogItemResponse)
                .toList();

        return new PageResponse<>(
                backlogItemResponses,
                backlogItems.getNumber(),
                backlogItems.getSize(),
                backlogItems.getTotalElements(),
                backlogItems.getTotalPages(),
                backlogItems.isFirst(),
                backlogItems.isLast()
        );
    }

    public PageResponse<BacklogItemResponse> findByProductBacklog(Integer productBacklogId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("priorite").ascending());
        Page<BacklogItem> backlogItems = backlogItemRepository.findByProductBacklogId(productBacklogId, pageable);
        List<BacklogItemResponse> backlogItemResponses = backlogItems.stream()
                .map(backlogItemMapper::toBacklogItemResponse)
                .toList();

        return new PageResponse<>(
                backlogItemResponses,
                backlogItems.getNumber(),
                backlogItems.getSize(),
                backlogItems.getTotalElements(),
                backlogItems.getTotalPages(),
                backlogItems.isFirst(),
                backlogItems.isLast()
        );
    }

    public PageResponse<BacklogItemResponse> findByProjectId(Integer projectId, int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());

        // Verify project exists
        Projet projet = projetRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + projectId));

        boolean isProductOwner = projet.getProductOwner() != null && projet.getProductOwner().getId().equals(user.getId());
        boolean isScrumMaster = projet.getScrumMaster() != null && projet.getScrumMaster().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isProductOwner && !isScrumMaster && !isAdmin) {
            throw new OperationNotPermittedException("Seul le Product Owner, le Scrum Master ou l'Admin peuvent accéder aux items du backlog de ce projet");
        }

        // Get product backlog for the project
        ProductBacklog productBacklog = productBacklogRepository.findByProjectId(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Product Backlog non trouvé pour le projet ID: " + projectId));

        // Get backlog items for the product backlog
        Pageable pageable = PageRequest.of(page, size, Sort.by("priorite").ascending());
        Page<BacklogItem> backlogItems = backlogItemRepository.findByProductBacklogId(productBacklog.getId(), pageable);
        List<BacklogItemResponse> backlogItemResponses = backlogItems.stream()
                .map(backlogItemMapper::toBacklogItemResponse)
                .toList();

        return new PageResponse<>(
                backlogItemResponses,
                backlogItems.getNumber(),
                backlogItems.getSize(),
                backlogItems.getTotalElements(),
                backlogItems.getTotalPages(),
                backlogItems.isFirst(),
                backlogItems.isLast()
        );
    }

    public PageResponse<BacklogItemResponse> findByStatut(itemstatut statut, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("priorite").ascending());
        Page<BacklogItem> backlogItems = backlogItemRepository.findByStatut(statut, pageable);
        List<BacklogItemResponse> backlogItemResponses = backlogItems.stream()
                .map(backlogItemMapper::toBacklogItemResponse)
                .toList();

        return new PageResponse<>(
                backlogItemResponses,
                backlogItems.getNumber(),
                backlogItems.getSize(),
                backlogItems.getTotalElements(),
                backlogItems.getTotalPages(),
                backlogItems.isFirst(),
                backlogItems.isLast()
        );
    }

    public List<BacklogItemResponse> findByPriorityOrder(Integer productBacklogId, boolean ascending) {
        List<BacklogItem> backlogItems;
        if (ascending) {
            backlogItems = backlogItemRepository.findByProductBacklogIdOrderByPrioriteAsc(productBacklogId);
        } else {
            backlogItems = backlogItemRepository.findByProductBacklogIdOrderByPrioriteDesc(productBacklogId);
        }

        return backlogItems.stream()
                .map(backlogItemMapper::toBacklogItemResponse)
                .toList();
    }

    public PageResponse<BacklogItemResponse> searchBacklogItems(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("priorite").ascending());
        Page<BacklogItem> backlogItems = backlogItemRepository.searchByTitleOrDescription(searchTerm, pageable);
        List<BacklogItemResponse> backlogItemResponses = backlogItems.stream()
                .map(backlogItemMapper::toBacklogItemResponse)
                .toList();

        return new PageResponse<>(
                backlogItemResponses,
                backlogItems.getNumber(),
                backlogItems.getSize(),
                backlogItems.getTotalElements(),
                backlogItems.getTotalPages(),
                backlogItems.isFirst(),
                backlogItems.isLast()
        );
    }

    public PageResponse<BacklogItemResponse> findWithFilters(
            String titre, itemstatut statut, Integer productBacklogId,
            Integer minPriorite, Integer maxPriorite, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("priorite").ascending());
        Page<BacklogItem> backlogItems = backlogItemRepository.findWithFilters(
                titre, statut, productBacklogId, minPriorite, maxPriorite, pageable);

        List<BacklogItemResponse> backlogItemResponses = backlogItems.stream()
                .map(backlogItemMapper::toBacklogItemResponse)
                .toList();

        return new PageResponse<>(
                backlogItemResponses,
                backlogItems.getNumber(),
                backlogItems.getSize(),
                backlogItems.getTotalElements(),
                backlogItems.getTotalPages(),
                backlogItems.isFirst(),
                backlogItems.isLast()
        );
    }

    public BacklogItemResponse updateBacklogItem(Integer id, BacklogItemRequest request) {
        var backlogItem = backlogItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Backlog Item non trouvé avec l'ID: " + id));

        // Check if the priority is being changed and handle conflicts
        if (!backlogItem.getPriorite().equals(request.getPriorite())) {
            if (backlogItemRepository.existsByProductBacklogIdAndPriorite(request.getProductBacklogId(), request.getPriorite())) {
                adjustPriorities(request.getProductBacklogId(), request.getPriorite());
            }
        }

        // Verify the product backlog exists if it's being changed
        if (!backlogItem.getProductBacklog().getId().equals(request.getProductBacklogId())) {
            productBacklogRepository.findById(request.getProductBacklogId())
                    .orElseThrow(() -> new EntityNotFoundException("Product Backlog non trouvé avec l'ID: " + request.getProductBacklogId()));
        }

        backlogItemMapper.updateBacklogItemFromRequest(request, backlogItem);
        var updatedBacklogItem = backlogItemRepository.save(backlogItem);
        return backlogItemMapper.toBacklogItemResponse(updatedBacklogItem);
    }

    public BacklogItemResponse changeStatut(Integer id, itemstatut newStatut) {
        var backlogItem = backlogItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Backlog Item non trouvé avec l'ID: " + id));

        // Validate status transition
        validateStatusTransition(backlogItem.getStatut(), newStatut);

        backlogItem.setStatut(newStatut);
        var updatedBacklogItem = backlogItemRepository.save(backlogItem);
        return backlogItemMapper.toBacklogItemResponse(updatedBacklogItem);
    }

    public BacklogItemResponse updatePriorite(Integer id, Integer newPriorite) {
        var backlogItem = backlogItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Backlog Item non trouvé avec l'ID: " + id));

        if (newPriorite < 1 || newPriorite > 100) {
            throw new OperationNotPermittedException("La priorité doit être entre 1 et 100");
        }

        // Handle priority conflicts
        if (backlogItemRepository.existsByProductBacklogIdAndPriorite(backlogItem.getProductBacklog().getId(), newPriorite)) {
            adjustPriorities(backlogItem.getProductBacklog().getId(), newPriorite);
        }

        backlogItem.setPriorite(newPriorite);
        var updatedBacklogItem = backlogItemRepository.save(backlogItem);
        return backlogItemMapper.toBacklogItemResponse(updatedBacklogItem);
    }

    public void deleteBacklogItem(Integer id) {
        var backlogItem = backlogItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Backlog Item non trouvé avec l'ID: " + id));

        // Optional: Prevent deletion of items in progress or completed
        if (backlogItem.getStatut() == itemstatut.EN_COURS) {
            throw new OperationNotPermittedException("Impossible de supprimer un item en cours");
        }

        backlogItemRepository.deleteById(id);
    }

    private void validateStatusTransition(itemstatut currentStatus, itemstatut newStatus) {
        // Define valid transitions
        boolean isValidTransition = switch (currentStatus) {
            case A_FAIRE -> newStatus == itemstatut.EN_COURS;
            case EN_COURS -> newStatus == itemstatut.TERMINE || newStatus == itemstatut.A_FAIRE;
            case TERMINE -> newStatus == itemstatut.EN_COURS; // Allow reopening
        };

        if (!isValidTransition) {
            throw new OperationNotPermittedException(
                    String.format("Transition de statut invalide: %s -> %s", currentStatus, newStatus)
            );
        }
    }

    private void adjustPriorities(Integer productBacklogId, Integer newPriority) {
        // Get all items with priority >= newPriority and increment them
        var itemsToAdjust = backlogItemRepository.findByProductBacklogId(productBacklogId)
                .stream()
                .filter(item -> item.getPriorite() >= newPriority)
                .toList();

        for (var item : itemsToAdjust) {
            item.setPriorite(item.getPriorite() + 1);
        }

        backlogItemRepository.saveAll(itemsToAdjust);
    }
}
