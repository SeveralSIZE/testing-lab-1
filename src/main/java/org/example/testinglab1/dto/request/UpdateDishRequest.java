package org.example.testinglab1.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.testinglab1.enums.DishCategory;
import org.example.testinglab1.enums.Flag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UpdateDishRequest {
    @Size(min = 2, max = 255)
    private String name;

    @Size(max = 5)
    private List<String> photos;

    @PositiveOrZero
    private Double calories;

    @PositiveOrZero
    private Double proteins;

    @PositiveOrZero
    private Double fats;

    @PositiveOrZero
    private Double carbohydrates;

    @Positive
    private Double portionSize;

    private DishCategory category;

    private Set<Flag> flags;

    @Size(min = 1)
    @Valid
    private List<IngredientRequest> ingredients;
}
