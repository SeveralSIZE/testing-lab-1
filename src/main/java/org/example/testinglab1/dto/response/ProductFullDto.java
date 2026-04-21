package org.example.testinglab1.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.enums.ProductCategory;
import org.example.testinglab1.enums.Readiness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class ProductFullDto {
    private UUID id;
    private String name;
    private List<String> photos;
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private String composition;
    private ProductCategory category;
    private Readiness readiness;
    private Set<Flag> flags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
