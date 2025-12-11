package idvey.testapi.notif;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationResponse {

    private Integer id;
    private String title;
    private String message;
    private LocalDateTime dateEnvoi;
    private Boolean isRead;
    private NotificationType type;
    private Integer userId;
    private String userFullName;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
