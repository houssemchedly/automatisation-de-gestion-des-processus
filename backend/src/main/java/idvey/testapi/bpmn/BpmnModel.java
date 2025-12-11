package idvey.testapi.bpmn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import idvey.testapi.common.BaseEntity;
import idvey.testapi.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bpmn_models")
@EntityListeners(AuditingEntityListener.class)
public class BpmnModel  {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate dateDebut;


    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String key;

    @Column(columnDefinition = "TEXT")
    private String bpmnXml;

    @Column(columnDefinition = "TEXT")
    private String diagram;

    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "last_modified_by")
    private User lastModifiedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BpmnStatus status;

    private String deploymentId;

    private Long version;

    @JsonIgnore
    @OneToMany(mappedBy = "bpmnModel", cascade = CascadeType.ALL)
    private List<ProcessInstance> processInstances;

    public enum BpmnStatus {
        DRAFT, DEPLOYED, ACTIVE, ARCHIVED
    }
}
