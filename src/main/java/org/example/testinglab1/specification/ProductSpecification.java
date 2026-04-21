package org.example.testinglab1.specification;

import org.example.testinglab1.dto.filter.ProductFilter;
import org.example.testinglab1.entity.Product;
import org.example.testinglab1.enums.Flag;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ProductSpecification {
    public Specification<Product> getSpecification(ProductFilter filter){
        var specificationPredicates = new ArrayList<Specification<Product>>();
        if(filter == null) return Specification.allOf();

        if(filter.getCategory() != null){
            specificationPredicates.add(((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category"), filter.getCategory())));
        }
        if(filter.getReadiness() != null){
            specificationPredicates.add(((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("readiness"), filter.getReadiness())));
        }
        if(filter.getFlags() != null && !filter.getFlags().isEmpty()){
            for (Flag flag : filter.getFlags()) {
                specificationPredicates.add((root, query, criteriaBuilder) ->
                        criteriaBuilder.isMember(flag, root.get("flags")));
            }
        }

        return Specification.allOf(specificationPredicates);
    }
}
