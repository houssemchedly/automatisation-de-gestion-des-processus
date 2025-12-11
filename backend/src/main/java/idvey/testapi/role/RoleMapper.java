package idvey.testapi.role;

import org.springframework.stereotype.Service;

@Service
public class RoleMapper {

    public RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .createdDate(role.getCreatedDate())
                .lastModifiedDate(role.getLastModifiedDate())
                .userCount(role.getUsers() != null ? role.getUsers().size() : 0)
                .build();
    }
}
