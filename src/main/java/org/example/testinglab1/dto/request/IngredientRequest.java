package org.example.testinglab1.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class IngredientRequest{
    @NotNull
    private UUID productId;

    @NotNull
    @Positive
    private Double amount;
}
