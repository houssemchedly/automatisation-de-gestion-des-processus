package idvey.testapi.projet;


import idvey.testapi.common.PageResponse;
import idvey.testapi.exception.OperationNotPermittedException;
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


import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ProjetService {
    private final ProjetRepository projetRepository;
    private final ProjetMapper projetMapper;
    private final UserRepository userRepository;

    public Integer save(ProjetRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Projet projet = projetMapper.toProjet(request);

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));

        boolean hasProductOwnerRole = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("PRODUCT_OWNER"));

        if (isAdmin) {
            if (request.productOwnerId() != null) {
                User productOwner = userRepository.findById(request.productOwnerId())
                        .orElseThrow(() -> new EntityNotFoundException("Product Owner not found with ID: " + request.productOwnerId()));
                projet.setProductOwner(productOwner);
            }
            if (request.scrumMasterId() != null) {
                User scrumMaster = userRepository.findById(request.scrumMasterId())
                        .orElseThrow(() -> new EntityNotFoundException("Scrum Master not found with ID: " + request.scrumMasterId()));
                projet.setScrumMaster(scrumMaster);
            }
            if (request.equipeIds() != null && !request.equipeIds().isEmpty()) {
                List<User> equipe = userRepository.findAllById(request.equipeIds());
                if (equipe.size() != request.equipeIds().size()) {
                    throw new EntityNotFoundException("Some team members not found");
                }
                projet.setEquipe(equipe);
            }
        } else if (hasProductOwnerRole) {
            projet.setProductOwner(user);
            if (request.equipeIds() != null && !request.equipeIds().isEmpty()) {
                List<User> equipe = userRepository.findAllById(request.equipeIds());
                if (equipe.size() != request.equipeIds().size()) {
                    throw new EntityNotFoundException("Some team members not found");
                }
                projet.setEquipe(equipe);
            }
        } else {
            throw new OperationNotPermittedException("Only users with PRODUCT_OWNER role or ADMIN role can create projects");
        }

        projet.setId(null);

        if (projet.getDateDebut() == null) {
            projet.setDateDebut(LocalDate.now());
        }

        if (projet.getStatut() == null) {
            projet.setStatut(ProjetStatut.A_FAIRE);
        }

        return projetRepository.save(projet).getId();
    }

    public ProjetResponse findById(Integer projetId) {
        return projetRepository.findById(projetId)
                .map(projetMapper::toProjetResponse)
                .orElseThrow(() -> new EntityNotFoundException("aucun projet trouvé avec cet identifiant:: " + projetId));
    }

    public PageResponse<ProjetResponse> findAllProjets(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDebut").descending());
        Page<Projet> projets = projetRepository.findAllDisplayableProjets(pageable, user.getId());
        List<ProjetResponse> projetResponse = projets.stream()
                .map(projetMapper::toProjetResponse)
                .toList();
        return new PageResponse<>(projetResponse, projets.getNumber(), projets.getSize(), projets.getTotalElements(), projets.getTotalPages(), projets.isFirst(), projets.isLast());
    }

    public PageResponse<ProjetResponse> findAllProjetsByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDebut").descending());
        Page<Projet> projets = projetRepository.findAllProjetsByOwner(pageable, user.getId());
        List<ProjetResponse> projetResponse = projets.stream()
                .map(projetMapper::toProjetResponse)
                .toList();
        return new PageResponse<>(projetResponse, projets.getNumber(), projets.getSize(), projets.getTotalElements(), projets.getTotalPages(), projets.isFirst(), projets.isLast());
    }

    public PageResponse<ProjetResponse> findAllProjetsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDebut").descending());
        Page<Projet> projets = projetRepository.findAll(pageable);
        List<ProjetResponse> projetResponse = projets.stream()
                .map(projetMapper::toProjetResponse)
                .toList();
        return new PageResponse<>(projetResponse, projets.getNumber(), projets.getSize(), projets.getTotalElements(), projets.getTotalPages(), projets.isFirst(), projets.isLast());
    }

    public PageResponse<ProjetResponse> findAllProjetsByScrumMaster(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDebut").descending());
        Page<Projet> projets = projetRepository.findAllProjetsByScrumMaster(pageable, user.getId());
        List<ProjetResponse> projetResponse = projets.stream()
                .map(projetMapper::toProjetResponse)
                .toList();
        return new PageResponse<>(projetResponse, projets.getNumber(), projets.getSize(), projets.getTotalElements(), projets.getTotalPages(), projets.isFirst(), projets.isLast());
    }

    public PageResponse<ProjetResponse> findAllProjetsByScrumMasterId(Integer scrumMasterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDebut").descending());
        Page<Projet> projets = projetRepository.findAllProjetsByScrumMaster(pageable, scrumMasterId);
        List<ProjetResponse> projetResponse = projets.stream()
                .map(projetMapper::toProjetResponse)
                .toList();
        return new PageResponse<>(projetResponse, projets.getNumber(), projets.getSize(), projets.getTotalElements(), projets.getTotalPages(), projets.isFirst(), projets.isLast());
    }

    public Integer updateActifStatus(Integer projetId, Authentication connectedUser) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun projet trouvé avec cet id : " + projetId));
        User user = ((User) connectedUser.getPrincipal());

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            projet.setActif(!projet.isActif());
            projetRepository.save(projet);
            return projetId;
        }

        boolean hasProductOwnerRole = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("PRODUCT_OWNER"));

        if (hasProductOwnerRole) {
            if (projet.getProductOwner() == null || !Objects.equals(projet.getProductOwner().getId(), user.getId())) {
                throw new OperationNotPermittedException("Vous ne pouvez modifier que les projets dont vous êtes le product owner");
            }
            projet.setActif(!projet.isActif());
            projetRepository.save(projet);
            return projetId;
        }

        throw new OperationNotPermittedException("Vous n'avez pas les permissions nécessaires pour modifier ce projet");
    }

    public ProjetResponse updateProjet(Integer projetId, ProjetRequest request, Authentication connectedUser) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun projet trouvé avec cet id : " + projetId));
        User user = ((User) connectedUser.getPrincipal());

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            projet.setNom(request.nom());
            projet.setDescription(request.description());
            projet.setActif(request.actif());
            projet.setDateDebut(request.dateDebut());

            if (request.productOwnerId() != null) {
                User productOwner = userRepository.findById(request.productOwnerId())
                        .orElseThrow(() -> new EntityNotFoundException("Product Owner not found with ID: " + request.productOwnerId()));
                projet.setProductOwner(productOwner);
            } else {
                projet.setProductOwner(null);
            }

            if (request.scrumMasterId() != null) {
                User scrumMaster = userRepository.findById(request.scrumMasterId())
                        .orElseThrow(() -> new EntityNotFoundException("Scrum Master not found with ID: " + request.scrumMasterId()));
                projet.setScrumMaster(scrumMaster);
            } else {
                projet.setScrumMaster(null);
            }

            if (request.equipeIds() != null) {
                if (request.equipeIds().isEmpty()) {
                    projet.setEquipe(null);
                } else {
                    List<User> equipe = userRepository.findAllById(request.equipeIds());
                    if (equipe.size() != request.equipeIds().size()) {
                        throw new EntityNotFoundException("Some team members not found");
                    }
                    projet.setEquipe(equipe);
                }
            }

            Projet updatedProjet = projetRepository.save(projet);
            return projetMapper.toProjetResponse(updatedProjet);
        }

        boolean hasProductOwnerRole = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("PRODUCT_OWNER"));

        if (hasProductOwnerRole) {
            if (projet.getProductOwner() == null || !Objects.equals(projet.getProductOwner().getId(), user.getId())) {
                throw new OperationNotPermittedException("Vous ne pouvez modifier que les projets dont vous êtes le product owner");
            }

            projet.setNom(request.nom());
            projet.setDescription(request.description());
            projet.setActif(request.actif());
            projet.setDateDebut(request.dateDebut());

            if (request.equipeIds() != null) {
                if (request.equipeIds().isEmpty()) {
                    projet.setEquipe(null);
                } else {
                    List<User> equipe = userRepository.findAllById(request.equipeIds());
                    if (equipe.size() != request.equipeIds().size()) {
                        throw new EntityNotFoundException("Some team members not found");
                    }
                    projet.setEquipe(equipe);
                }
            }

            Projet updatedProjet = projetRepository.save(projet);
            return projetMapper.toProjetResponse(updatedProjet);
        }

        throw new OperationNotPermittedException("Vous n'avez pas les permissions nécessaires pour modifier ce projet");
    }

    public void deleteProjet(Integer projetId, Authentication connectedUser) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun projet trouvé avec cet id : " + projetId));

        User user = ((User) connectedUser.getPrincipal());

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            projetRepository.delete(projet);
            return;
        }

        boolean hasProductOwnerRole = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("PRODUCT_OWNER"));

        if (hasProductOwnerRole) {
            if (projet.getProductOwner() == null || !Objects.equals(projet.getProductOwner().getId(), user.getId())) {
                throw new OperationNotPermittedException("Vous ne pouvez supprimer que les projets dont vous êtes le product owner");
            }
            projetRepository.delete(projet);
            return;
        }

        throw new OperationNotPermittedException("Vous n'avez pas les permissions nécessaires pour supprimer ce projet");
    }

    public void addTeamMembers(Integer projetId, List<Integer> userIds, Authentication connectedUser) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun projet trouvé avec cet id : " + projetId));

        User user = ((User) connectedUser.getPrincipal());
        validateProjectAccess(projet, user);

        List<User> newMembers = userRepository.findAllById(userIds);
        if (newMembers.size() != userIds.size()) {
            throw new EntityNotFoundException("Some users not found");
        }

        if (projet.getEquipe() == null) {
            projet.setEquipe(newMembers);
        } else {
            // Add only new members (avoid duplicates)
            for (User newMember : newMembers) {
                if (!projet.getEquipe().contains(newMember)) {
                    projet.getEquipe().add(newMember);
                }
            }
        }

        projetRepository.save(projet);
    }

    public void removeTeamMembers(Integer projetId, List<Integer> userIds, Authentication connectedUser) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun projet trouvé avec cet id : " + projetId));

        User user = ((User) connectedUser.getPrincipal());
        validateProjectAccess(projet, user);

        if (projet.getEquipe() != null) {
            projet.getEquipe().removeIf(member -> userIds.contains(member.getId()));
            projetRepository.save(projet);
        }
    }

    public List<ProjetTeamMemberResponse> getTeamMembers(Integer projetId, Authentication connectedUser) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun projet trouvé avec cet id : " + projetId));

        User user = ((User) connectedUser.getPrincipal());
        validateProjectAccess(projet, user);

        if (projet.getEquipe() == null) {
            return List.of();
        }

        return projet.getEquipe().stream()
                .map(member -> ProjetTeamMemberResponse.builder()
                        .id(member.getId())
                        .nom(member.getNom())
                        .prenom(member.getPrenom())
                        .email(member.getEmail())
                        .accountLocked(member.isAccountNonLocked())
                        .enabled(member.isEnabled())
                        .build())
                .toList();
    }

    private void validateProjectAccess(Projet projet, User user) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            return; // Admin can access any project
        }

        boolean isProductOwner = projet.getProductOwner() != null &&
                Objects.equals(projet.getProductOwner().getId(), user.getId());

        boolean isScrumMaster = projet.getScrumMaster() != null &&
                Objects.equals(projet.getScrumMaster().getId(), user.getId());

        if (!isProductOwner && !isScrumMaster) {
            throw new OperationNotPermittedException("Vous n'avez pas les permissions nécessaires pour gérer ce projet");
        }
    }
}
