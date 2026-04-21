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
public class CreateDishRequest {
    @NotBlank
    @Size(min = 2, max = 255)
    private String name;

    @Size(max = 5)
    private List<String> photos;

    @NotNull
    @PositiveOrZero
    private Double calories;

    @NotNull
    @PositiveOrZero
    private Double proteins;

    @NotNull
    @PositiveOrZero
    private Double fats;

    @NotNull
    @PositiveOrZero
    private Double carbohydrates;

    @NotNull
    @Positive
    private Double portionSize;

    @NotNull
    private DishCategory category;

    private Set<Flag> flags;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<IngredientRequest> ingredients;

    @Data
    @NoArgsConstructor
    public static class IngredientRequest{
        @NotNull
        private UUID productId;

        @NotNull
        @Positive
        private Double amount;
    }
}
