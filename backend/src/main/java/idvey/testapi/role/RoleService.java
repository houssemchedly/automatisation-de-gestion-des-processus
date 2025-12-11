package idvey.testapi.role;

import idvey.testapi.user.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    public RoleResponse findById(Integer roleId) {
        return roleRepository.findById(roleId)
                .map(roleMapper::toRoleResponse)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
    }

    public List<UserSummaryResponse> findUsersByRole(Integer roleId) {
        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        return role.getUsers()
                .stream()
                .map(user -> UserSummaryResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .enabled(user.isEnabled())
                        .accountLocked(user.isAccountlocked())
                        .build())
                .collect(Collectors.toList());
    }
}
