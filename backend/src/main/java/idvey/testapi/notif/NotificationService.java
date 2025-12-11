package idvey.testapi.notif;

import idvey.testapi.common.PageResponse;
import idvey.testapi.user.User;
import idvey.testapi.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public Integer save(NotificationRequest request, Authentication connectedUser) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        var notification = notificationMapper.toNotification(request);
        notification.setUser(user);

        return notificationRepository.save(notification).getId();
    }

    public NotificationResponse findById(Integer notificationId, Authentication connectedUser) {
        return notificationRepository.findById(notificationId)
                .map(notification -> {
                    // Check if the user can access this notification
                    User user = ((User) connectedUser.getPrincipal());
                    if (!Objects.equals(notification.getUser().getId(), user.getId()) &&
                            user.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
                        throw new IllegalArgumentException("You can only access your own notifications");
                    }
                    return notificationMapper.toNotificationResponse(notification);
                })
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
    }

    public PageResponse<NotificationResponse> findAllByUser(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notifications = notificationRepository.findByUserIdOrderByDateEnvoiDesc(
                user.getId(), pageable);

        List<NotificationResponse> notificationResponses = notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();

        return new PageResponse<>(
                notificationResponses,
                notifications.getNumber(),
                notifications.getSize(),
                notifications.getTotalElements(),
                notifications.getTotalPages(),
                notifications.isFirst(),
                notifications.isLast()
        );
    }

    public PageResponse<NotificationResponse> findByReadStatus(Boolean isRead, int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notifications = notificationRepository.findByUserIdAndIsReadOrderByDateEnvoiDesc(
                user.getId(), isRead, pageable);

        List<NotificationResponse> notificationResponses = notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();

        return new PageResponse<>(
                notificationResponses,
                notifications.getNumber(),
                notifications.getSize(),
                notifications.getTotalElements(),
                notifications.getTotalPages(),
                notifications.isFirst(),
                notifications.isLast()
        );
    }

    public PageResponse<NotificationResponse> findByType(NotificationType type, int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notifications = notificationRepository.findByUserIdAndTypeOrderByDateEnvoiDesc(
                user.getId(), type, pageable);

        List<NotificationResponse> notificationResponses = notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();

        return new PageResponse<>(
                notificationResponses,
                notifications.getNumber(),
                notifications.getSize(),
                notifications.getTotalElements(),
                notifications.getTotalPages(),
                notifications.isFirst(),
                notifications.isLast()
        );
    }

    public PageResponse<NotificationResponse> searchNotifications(
            Boolean isRead, NotificationType type, LocalDateTime startDate, LocalDateTime endDate,
            int page, int size, Authentication connectedUser) {

        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notifications = notificationRepository.findByUserIdWithFilters(
                user.getId(), isRead, type, startDate, endDate, pageable);

        List<NotificationResponse> notificationResponses = notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();

        return new PageResponse<>(
                notificationResponses,
                notifications.getNumber(),
                notifications.getSize(),
                notifications.getTotalElements(),
                notifications.getTotalPages(),
                notifications.isFirst(),
                notifications.isLast()
        );
    }

    public Integer update(Integer notificationId, NotificationRequest request, Authentication connectedUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(notification.getUser().getId(), user.getId()) &&
                user.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new IllegalArgumentException("You can only update your own notifications");
        }

        notificationMapper.updateNotificationFromRequest(request, notification);
        return notificationRepository.save(notification).getId();
    }

    public void markAsRead(Integer notificationId, Authentication connectedUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(notification.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("You can only mark your own notifications as read");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void markAsUnread(Integer notificationId, Authentication connectedUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(notification.getUser().getId(), user.getId())) {
            throw new IllegalArgumentException("You can only mark your own notifications as unread");
        }

        notification.setIsRead(false);
        notificationRepository.save(notification);
    }

    public int markAllAsRead(Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        return notificationRepository.markAllAsReadByUserId(user.getId());
    }

    public int markMultipleAsRead(List<Integer> notificationIds, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        return notificationRepository.updateReadStatusByIds(notificationIds, true, user.getId());
    }

    public long getUnreadCount(Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        return notificationRepository.countByUserIdAndIsRead(user.getId(), false);
    }

    public void delete(Integer notificationId, Authentication connectedUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(notification.getUser().getId(), user.getId()) &&
                user.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals("ADMIN"))) {
            throw new IllegalArgumentException("You can only delete your own notifications");
        }

        notificationRepository.delete(notification);
    }

    public void deleteOldNotifications(int daysOld, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        notificationRepository.deleteByUserIdAndDateEnvoiBefore(user.getId(), cutoffDate);
    }
}
