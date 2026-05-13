package org.example.testinglab1.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class GetDishNutritionRequest {
    @NotNull
    private List<UUID> ingredientsIds;

    @NotNull
    private List<Integer> ingredientsAmounts;
}
