package idvey.testapi.notif;

import idvey.testapi.common.BaseEntity;
import idvey.testapi.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification extends BaseEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime dateEnvoi;

    @Builder.Default
    private Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationType type = NotificationType.INFO;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
