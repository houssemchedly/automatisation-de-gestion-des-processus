package idvey.testapi.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSummaryResponse {
    private Integer id;
    private String fullName;
    private String email;
    private boolean enabled;
    private boolean accountLocked;
}
