package idvey.testapi.productBacklog;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductBacklogRequest {

    // Getters and setters
    @NotNull(message = "Project ID is required")
    private Integer projetId;

}
