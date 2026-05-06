package org.example.testinglab1.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DishProductDto {
    private UUID productId;
    private String productName;
    private Double amount;
}
