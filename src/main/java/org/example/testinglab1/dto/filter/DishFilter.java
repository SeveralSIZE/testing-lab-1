package org.example.testinglab1.dto.filter;

import lombok.Data;
import org.example.testinglab1.enums.DishCategory;
import org.example.testinglab1.enums.Flag;

import java.util.Set;

@Data
public class DishFilter {
    private String name;
    private Set<DishCategory> categories;
    private Set<Flag> flags;
}
