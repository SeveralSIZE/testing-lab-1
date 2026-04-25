package org.example.testinglab1.dto.filter;

import lombok.Data;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.enums.ProductCategory;
import org.example.testinglab1.enums.Readiness;

import java.util.Set;


@Data
public class ProductFilter {
    private String name;
    private Set<ProductCategory> categories;
    private Set<Readiness> readiness;
    private Set<Flag> flags;
}
