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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductBacklogService {

    private final ProductBacklogRepository productBacklogRepository;
    private final ProductBacklogMapper productBacklogMapper;
    private final ProjetRepository projetRepository;

    public Integer save(ProductBacklogRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Projet projet = projetRepository.findById(request.getProjetId())
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + request.getProjetId()));

        boolean isProductOwner = projet.getProductOwner() != null && projet.getProductOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isProductOwner && !isAdmin) {
            throw new OperationNotPermittedException("Seul le Product Owner ou l'Admin peut créer le Product Backlog");
        }

        // Check if the product-backlog already exists for this project
        if (productBacklogRepository.findByProjectId(request.getProjetId()).isPresent()) {
            throw new OperationNotPermittedException("Un Product Backlog existe déjà pour ce projet");
        }

        ProductBacklog productBacklog = productBacklogMapper.toProductBacklog(request);
        productBacklog.setProjet(projet);
        return productBacklogRepository.save(productBacklog).getId();
    }

    public ProductBacklogResponse findById(Integer backlogId, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        ProductBacklog productBacklog = productBacklogRepository.findById(backlogId)
                .orElseThrow(() -> new EntityNotFoundException("Product Backlog non trouvé avec l'ID: " + backlogId));

        boolean isProductOwner = productBacklog.getProjet().getProductOwner() != null && productBacklog.getProjet().getProductOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isProductOwner && !isAdmin) {
            throw new OperationNotPermittedException("Seul le Product Owner ou l'Admin peut consulter ce Product Backlog");
        }

        return productBacklogMapper.toProductBacklogResponse(productBacklog);
    }

    public ProductBacklogResponse findByProjectId(Integer projectId, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Projet projet = projetRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + projectId));

        boolean isProductOwner = projet.getProductOwner() != null && projet.getProductOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isProductOwner && !isAdmin) {
            throw new OperationNotPermittedException("Seul le Product Owner ou l'Admin peut consulter ce Product Backlog");
        }

        ProductBacklog productBacklog = productBacklogRepository.findByProjectId(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Product Backlog non trouvé pour le projet ID: " + projectId));

        return productBacklogMapper.toProductBacklogResponse(productBacklog);
    }

    public PageResponse<ProductBacklogResponse> findAllProductBacklogs(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size);

        Page<ProductBacklog> productBacklogs;
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        if (isAdmin) {
            productBacklogs = productBacklogRepository.findAll(pageable);
        } else {
            productBacklogs = productBacklogRepository.findAllByProductOwner(pageable, user.getId());
        }

        List<ProductBacklogResponse> productBacklogResponses = productBacklogs.stream()
                .map(productBacklogMapper::toProductBacklogResponse)
                .toList();

        return new PageResponse<>(
                productBacklogResponses,
                productBacklogs.getNumber(),
                productBacklogs.getSize(),
                productBacklogs.getTotalElements(),
                productBacklogs.getTotalPages(),
                productBacklogs.isFirst(),
                productBacklogs.isLast()
        );
    }

    public ProductBacklogResponse updateProductBacklog(Integer backlogId, ProductBacklogRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        ProductBacklog productBacklog = productBacklogRepository.findById(backlogId)
                .orElseThrow(() -> new EntityNotFoundException("Product Backlog non trouvé avec l'ID: " + backlogId));

        boolean isProductOwner = productBacklog.getProjet().getProductOwner() != null && productBacklog.getProjet().getProductOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isProductOwner && !isAdmin) {
            throw new OperationNotPermittedException("Seul le Product Owner ou l'Admin peut modifier ce Product Backlog");
        }

        // If the project is being changed, validate the new project
        if (!productBacklog.getProjet().getId().equals(request.getProjetId())) {
            Projet newProjet = projetRepository.findById(request.getProjetId())
                    .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + request.getProjetId()));

            if (!isAdmin && (newProjet.getProductOwner() == null || !newProjet.getProductOwner().getId().equals(user.getId()))) {
                throw new OperationNotPermittedException("Vous n'êtes pas le Product Owner du nouveau projet");
            }

            productBacklog.setProjet(newProjet);
        }

        ProductBacklog savedBacklog = productBacklogRepository.save(productBacklog);
        return productBacklogMapper.toProductBacklogResponse(savedBacklog);
    }

    public void deleteProductBacklog(Integer backlogId, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        ProductBacklog productBacklog = productBacklogRepository.findById(backlogId)
                .orElseThrow(() -> new EntityNotFoundException("Product Backlog non trouvé avec l'ID: " + backlogId));

        boolean isProductOwner = productBacklog.getProjet().getProductOwner() != null && productBacklog.getProjet().getProductOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));

        if (!isProductOwner && !isAdmin) {
            throw new OperationNotPermittedException("Seul le Product Owner ou l'Admin peut supprimer ce Product Backlog");
        }

        productBacklogRepository.delete(productBacklog);
    }
}
