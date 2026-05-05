package org.example.testinglab1.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class GetDishNutritionRequest {
    private List<UUID> ingredientsIds;
    private List<Integer> ingredientsAmounts;
    private Double portionSize;
}
