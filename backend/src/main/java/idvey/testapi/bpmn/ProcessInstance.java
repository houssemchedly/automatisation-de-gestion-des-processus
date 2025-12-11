package idvey.testapi.bpmn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import idvey.testapi.common.BaseEntity;
import idvey.testapi.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "process_instances")
@EntityListeners(AuditingEntityListener.class)
public class ProcessInstance {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String camundaProcessInstanceId;

    @ManyToOne
    @JoinColumn(name = "bpmn_model_id", nullable = false)
    private BpmnModel bpmnModel;

    @ManyToOne
    @JoinColumn(name = "started_by", nullable = false)
    private User startedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "processInstance", cascade = CascadeType.ALL)
    private List<UserTask> userTasks;

    public enum ProcessStatus {
        RUNNING, COMPLETED, SUSPENDED, TERMINATED
    }
}
