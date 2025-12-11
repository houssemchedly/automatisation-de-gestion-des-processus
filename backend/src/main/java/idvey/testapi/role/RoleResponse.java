package idvey.testapi.role;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RoleResponse {
    private Integer id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private int userCount; // Number of users with this role
}
