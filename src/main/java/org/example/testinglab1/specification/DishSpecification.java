package org.example.testinglab1.specification;

import org.example.testinglab1.dto.filter.DishFilter;
import org.example.testinglab1.dto.filter.ProductFilter;
import org.example.testinglab1.entity.Dish;
import org.example.testinglab1.entity.Product;
import org.example.testinglab1.enums.Flag;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DishSpecification {
    public Specification<Dish> getSpecification(DishFilter filter){
        var specificationPredicates = new ArrayList<Specification<Dish>>();
        if(filter == null) return Specification.allOf();

        if (filter.getName() != null && !filter.getName().isBlank()) {
            specificationPredicates.add((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
        }
        if(filter.getCategories() != null && !filter.getCategories().isEmpty()){
            Specification<Dish> categorySpec = Specification.anyOf(
                    filter.getCategories().stream()
                            .map(category -> (Specification<Dish>)
                                    (root, query, cb) -> cb.equal(root.get("category"), category))
                            .toList()
            );
            specificationPredicates.add(categorySpec);
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
