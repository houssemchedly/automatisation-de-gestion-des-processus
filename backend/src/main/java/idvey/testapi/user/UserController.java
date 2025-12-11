package idvey.testapi.user;

import idvey.testapi.common.PageResponse;
import idvey.testapi.role.Role;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "User Management")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> findAllUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "sort", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc", required = false) String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PageResponse<UserResponse> pageResponse = userService.findAllUsers(pageable);

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<UserResponse> findUserById(
            @PathVariable("user-id") Integer userId
    ) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{user-id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("user-id") Integer userId,
            @Valid @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("user-id") Integer userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{user-id}/toggle-status")
    public ResponseEntity<UserResponse> toggleUserStatus(
            @PathVariable("user-id") Integer userId
    ) {
        return ResponseEntity.ok(userService.toggleUserStatus(userId));
    }

    @PatchMapping("/{user-id}/toggle-lock")
    public ResponseEntity<UserResponse> toggleUserLock(
            @PathVariable("user-id") Integer userId
    ) {
        return ResponseEntity.ok(userService.toggleUserLock(userId));
    }

    @PostMapping("/{user-id}/roles/{role-id}")
    public ResponseEntity<UserResponse> assignRoleToUser(
            @PathVariable("user-id") Integer userId,
            @PathVariable("role-id") Integer roleId
    ) {
        return ResponseEntity.ok(userService.assignRoleToUser(userId, roleId));
    }

    @DeleteMapping("/{user-id}/roles/{role-id}")
    public ResponseEntity<UserResponse> removeRoleFromUser(
            @PathVariable("user-id") Integer userId,
            @PathVariable("role-id") Integer roleId
    ) {
        return ResponseEntity.ok(userService.removeRoleFromUser(userId, roleId));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(userService.getAllRoles());
    }
}
