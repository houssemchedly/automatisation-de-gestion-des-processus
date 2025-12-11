package idvey.testapi.notif;

import idvey.testapi.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("notifications")
@RequiredArgsConstructor
@Tag(name = "Notification")
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<Integer> saveNotification(
            @Valid @RequestBody NotificationRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @GetMapping("/{notification-id}")
    public ResponseEntity<NotificationResponse> findNotificationById(
            @PathVariable("notification-id") Integer notificationId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findById(notificationId, connectedUser));
    }

    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>> findAllNotifications(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findAllByUser(page, size, connectedUser));
    }

    @GetMapping("/unread")
    public ResponseEntity<PageResponse<NotificationResponse>> findUnreadNotifications(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findByReadStatus(false, page, size, connectedUser));
    }

    @GetMapping("/read")
    public ResponseEntity<PageResponse<NotificationResponse>> findReadNotifications(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findByReadStatus(true, page, size, connectedUser));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<PageResponse<NotificationResponse>> findNotificationsByType(
            @PathVariable NotificationType type,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.findByType(type, page, size, connectedUser));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<NotificationResponse>> searchNotifications(
            @RequestParam(name = "isRead", required = false) Boolean isRead,
            @RequestParam(name = "type", required = false) NotificationType type,
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.searchNotifications(isRead, type, startDate, endDate, page, size, connectedUser));
    }

    @PutMapping("/{notification-id}")
    public ResponseEntity<Integer> updateNotification(
            @PathVariable("notification-id") Integer notificationId,
            @Valid @RequestBody NotificationRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.update(notificationId, request, connectedUser));
    }

    @PatchMapping("/{notification-id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable("notification-id") Integer notificationId,
            Authentication connectedUser
    ) {
        service.markAsRead(notificationId, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{notification-id}/unread")
    public ResponseEntity<Void> markAsUnread(
            @PathVariable("notification-id") Integer notificationId,
            Authentication connectedUser
    ) {
        service.markAsUnread(notificationId, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<Integer> markAllAsRead(Authentication connectedUser) {
        int updatedCount = service.markAllAsRead(connectedUser);
        return ResponseEntity.ok(updatedCount);
    }

    @PatchMapping("/mark-multiple-read")
    public ResponseEntity<Integer> markMultipleAsRead(
            @RequestBody List<Integer> notificationIds,
            Authentication connectedUser
    ) {
        int updatedCount = service.markMultipleAsRead(notificationIds, connectedUser);
        return ResponseEntity.ok(updatedCount);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication connectedUser) {
        return ResponseEntity.ok(service.getUnreadCount(connectedUser));
    }

    @DeleteMapping("/{notification-id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable("notification-id") Integer notificationId,
            Authentication connectedUser
    ) {
        service.delete(notificationId, connectedUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> deleteOldNotifications(
            @RequestParam(name = "daysOld", defaultValue = "30") int daysOld,
            Authentication connectedUser
    ) {
        service.deleteOldNotifications(daysOld, connectedUser);
        return ResponseEntity.noContent().build();
    }
}
