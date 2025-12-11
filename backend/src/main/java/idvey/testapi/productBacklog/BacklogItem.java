package idvey.testapi.productBacklog;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BacklogItem {

    @Id
    @GeneratedValue
    private Integer id;
    private String titre;
    private String description;
    private Integer priorite;
    private Integer points;
    private itemstatut statut;
    @Column(name = "item_type")
    private itemType type;
    @ManyToOne
    @JoinColumn(name = "Product_Backlog_id")
    private ProductBacklog productBacklog;
}
