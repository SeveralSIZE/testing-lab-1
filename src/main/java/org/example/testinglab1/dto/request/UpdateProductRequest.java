package org.example.testinglab1.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.enums.ProductCategory;
import org.example.testinglab1.enums.Readiness;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class UpdateProductRequest {
    @Size(min = 2, max = 255)
    private String name;

    @Size(max = 5)
    private List<String> photos;

    @PositiveOrZero
    private Double calories;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double proteins;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double fats;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double carbohydrates;

    private String composition;

    private ProductCategory category;

    private Readiness readiness;

    private Set<Flag> flags;
}
