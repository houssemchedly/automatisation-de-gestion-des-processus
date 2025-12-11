package idvey.testapi.productBacklog;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductBacklogResponse {

    private Integer id;
    private Integer projectId;
    private String projectName;
    private String productOwnerName;
    private Integer backlogItemsCount;
}
