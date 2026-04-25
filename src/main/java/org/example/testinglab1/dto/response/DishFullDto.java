package org.example.testinglab1.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.testinglab1.enums.DishCategory;
import org.example.testinglab1.enums.Flag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class DishFullDto {
    private UUID id;
    private String name;
    private List<String> photos;
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private Double portionSize;
    private DishCategory category;
    private Set<Flag> flags;
    private List<DishProductDto> ingredients;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
