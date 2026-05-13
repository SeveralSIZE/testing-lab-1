package org.example.testinglab1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.testinglab1.enums.Flag;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NutritionDto {
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private Set<Flag> allowedFlags;
}
