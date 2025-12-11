package idvey.testapi.notif;

import idvey.testapi.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationMapper {

    public Notification toNotification(NotificationRequest request) {
        User user = new User();
        user.setId(request.getUserId());

        return Notification.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .dateEnvoi(LocalDateTime.now())
                .isRead(false)
                .user(user)
                .build();
    }

    public NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .dateEnvoi(notification.getDateEnvoi())
                .isRead(notification.getIsRead())
                .type(notification.getType())
                .userId(notification.getUser().getId())
                .userFullName(notification.getUser().getPrenom() + " " + notification.getUser().getNom())
                .createdDate(notification.getDateEnvoi())
                .build();
    }

    public void updateNotificationFromRequest(NotificationRequest request, Notification notification) {
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
    }
}
