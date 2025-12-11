package idvey.testapi.projet;

import org.springframework.data.jpa.domain.Specification;

public class ProjetSpecification {

    public static Specification<Projet> withOwnerId(Integer ownerId){
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("productOwner").get("id"), ownerId );
        }
}
