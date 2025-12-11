package idvey.testapi.user;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .dateNaissance(user.getDateNaissance())
                .enabled(user.isEnabled())
                .accountLocked(user.isAccountlocked())
                .createdDate(user.getCreatedDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .roles(user.getRoles() != null ?
                        user.getRoles().stream()
                                .map(role -> UserResponse.RoleResponse.builder()
                                        .id(role.getId())
                                        .name(role.getName())
                                        .createdDate(role.getCreatedDate())
                                        .lastModifiedDate(role.getLastModifiedDate())
                                        .build())
                                .collect(Collectors.toList()) : null)
                .build();
    }

    public User toUser(UserRequest request) {
        return User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .dateNaissance(request.getDateNaissance())
                .enabled(request.isEnabled())
                .accountlocked(request.isAccountLocked())
                .build();
    }
}
