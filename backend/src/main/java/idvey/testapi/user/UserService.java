package idvey.testapi.user;

import idvey.testapi.common.PageResponse;
import idvey.testapi.role.Role;
import idvey.testapi.role.RoleRepository;
import idvey.testapi.user.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final TokenRepository tokenRepository;

    public PageResponse<UserResponse> findAllUsers(Pageable pageable) {
        Page<UserResponse> page = userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);

        return PageResponse.<UserResponse>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    public UserResponse findById(Integer userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    public UserResponse createUser(UserRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        var user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set default role if not specified
        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            var userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default USER role not found"));
            user.setRoles(List.of(userRole));
        } else {
            var roles = roleRepository.findAllById(request.getRoleIds());
            user.setRoles(roles);
        }

        var savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    public UserResponse updateUser(Integer userId, UserRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Update user fields
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setDateNaissance(request.getDateNaissance());
        user.setEnabled(request.isEnabled());
        user.setAccountlocked(request.isAccountLocked());

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update roles if provided
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            var roles = roleRepository.findAllById(request.getRoleIds());
            user.setRoles(roles);
        }

        var updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }

    public void deleteUser(Integer userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        tokenRepository.deleteByUser(user);

        // Clear user roles to avoid foreign key constraint issues
        user.getRoles().clear();

        // Clear user from projects they are assigned to
        if (user.getProjets() != null) {
            for (var projet : user.getProjets()) {
                projet.getEquipe().remove(user);
            }
            user.getProjets().clear();
        }

        // Clear user from meetings they participate in
        if (user.getMeetings() != null) {
            for (var meeting : user.getMeetings()) {
                meeting.getParticipants().remove(user);
            }
            user.getMeetings().clear();
        }

        // Save user to persist relationship changes before deletion
        userRepository.save(user);

        // Now safely delete the user
        userRepository.deleteById(userId);
    }

    public UserResponse toggleUserStatus(Integer userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setEnabled(!user.isEnabled());
        var updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }

    public UserResponse toggleUserLock(Integer userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setAccountlocked(!user.isAccountlocked());
        var updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }

    public UserResponse assignRoleToUser(Integer userId, Integer roleId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            var updatedUser = userRepository.save(user);
            return userMapper.toUserResponse(updatedUser);
        }

        return userMapper.toUserResponse(user);
    }

    public UserResponse removeRoleFromUser(Integer userId, Integer roleId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        // Prevent removing the last USER role
        if (role.getName().equals("USER") && user.getRoles().size() == 1) {
            throw new RuntimeException("Cannot remove the last USER role from user");
        }

        user.getRoles().remove(role);
        var updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
