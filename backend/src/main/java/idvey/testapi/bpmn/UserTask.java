package idvey.testapi.bpmn;

import idvey.testapi.common.BaseEntity;
import idvey.testapi.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_tasks")
@EntityListeners(AuditingEntityListener.class)
public class UserTask {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate dateDebut;


    @Column(nullable = false)
    private String camundaTaskId;

    @ManyToOne
    @JoinColumn(name = "process_instance_id", nullable = false)
    private ProcessInstance processInstance;

    @Column(nullable = false)
    private String taskName;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    private LocalDateTime claimedAt;
    private LocalDateTime completedAt;

    public enum TaskStatus {
        PENDING, CLAIMED, COMPLETED, CANCELLED
    }
}
