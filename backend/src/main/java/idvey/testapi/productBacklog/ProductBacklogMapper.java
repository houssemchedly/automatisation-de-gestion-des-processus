package idvey.testapi.productBacklog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductBacklogMapper {

    private final ProductBacklogRepository productBacklogRepository;

    public ProductBacklog toProductBacklog(ProductBacklogRequest request) {
        return ProductBacklog.builder()
                .build();
    }

    public ProductBacklogResponse toProductBacklogResponse(ProductBacklog productBacklog) {
        return ProductBacklogResponse.builder()
                .id(productBacklog.getId())
                .projectId(productBacklog.getProjet().getId())
                .projectName(productBacklog.getProjet().getNom())
                .productOwnerName(productBacklog.getProjet().getProductOwner().getPrenom() + " " +
                        productBacklog.getProjet().getProductOwner().getNom())
                .backlogItemsCount(productBacklog.getItems() != null ? productBacklog.getItems().size() : 0)
                .build();
    }
}
