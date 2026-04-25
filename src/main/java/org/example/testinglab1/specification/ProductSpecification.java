package org.example.testinglab1.specification;

import lombok.extern.slf4j.Slf4j;
import org.example.testinglab1.dto.filter.ProductFilter;
import org.example.testinglab1.entity.Product;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.enums.ProductCategory;
import org.example.testinglab1.enums.Readiness;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class ProductSpecification {
    public Specification<Product> getSpecification(ProductFilter filter){
        var specificationPredicates = new ArrayList<Specification<Product>>();
        if(filter == null) return Specification.allOf();

        if (filter.getName() != null && !filter.getName().isBlank()) {
            specificationPredicates.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
        }
        if(filter.getCategories() != null && !filter.getCategories().isEmpty()){
            Specification<Product> categorySpec = Specification.anyOf(
                    filter.getCategories().stream()
                            .map(category -> (Specification<Product>)
                                    (root, query, cb) -> cb.equal(root.get("category"), category))
                            .toList()
            );
            specificationPredicates.add(categorySpec);
        }
        if(filter.getReadiness() != null && !filter.getReadiness().isEmpty()){
            Specification<Product> readinessSpec = Specification.anyOf(
                    filter.getReadiness().stream()
                            .map(readiness -> (Specification<Product>)
                                    (root, query, cb) -> cb.equal(root.get("readiness"), readiness))
                            .toList()
            );
            specificationPredicates.add(readinessSpec);
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
