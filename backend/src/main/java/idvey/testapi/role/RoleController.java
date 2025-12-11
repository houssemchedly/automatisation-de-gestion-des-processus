package idvey.testapi.role;

import idvey.testapi.user.UserSummaryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
@Tag(name = "Role Management")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.findAllRoles());
    }

    @GetMapping("/{role-id}")
    public ResponseEntity<RoleResponse> getRoleById(
            @PathVariable("role-id") Integer roleId
    ) {
        return ResponseEntity.ok(roleService.findById(roleId));
    }

    @GetMapping("/{role-id}/users")
    public ResponseEntity<List<UserSummaryResponse>> getUsersByRole(
            @PathVariable("role-id") Integer roleId
    ) {
        return ResponseEntity.ok(roleService.findUsersByRole(roleId));
    }
}
