package idvey.testapi.notif;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByUserIdOrderByDateEnvoiDesc(Integer userId, Pageable pageable);

    Page<Notification> findByUserIdAndIsReadOrderByDateEnvoiDesc(Integer userId, Boolean isRead, Pageable pageable);

    Page<Notification> findByUserIdAndTypeOrderByDateEnvoiDesc(Integer userId, NotificationType type, Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND " +
            "(:isRead IS NULL OR n.isRead = :isRead) AND " +
            "(:type IS NULL OR n.type = :type) AND " +
            "(:startDate IS NULL OR n.dateEnvoi >= :startDate) AND " +
            "(:endDate IS NULL OR n.dateEnvoi <= :endDate) " +
            "ORDER BY n.dateEnvoi DESC")
    Page<Notification> findByUserIdWithFilters(
            @Param("userId") Integer userId,
            @Param("isRead") Boolean isRead,
            @Param("type") NotificationType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    long countByUserIdAndIsRead(Integer userId, Boolean isRead);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Integer userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = :isRead WHERE n.id IN :ids AND n.user.id = :userId")
    int updateReadStatusByIds(@Param("ids") List<Integer> ids, @Param("isRead") Boolean isRead, @Param("userId") Integer userId);

    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.dateEnvoi < :cutoffDate")
    List<Notification> findOldNotificationsByUserId(@Param("userId") Integer userId, @Param("cutoffDate") LocalDateTime cutoffDate);

    void deleteByUserIdAndDateEnvoiBefore(Integer userId, LocalDateTime cutoffDate);
}
