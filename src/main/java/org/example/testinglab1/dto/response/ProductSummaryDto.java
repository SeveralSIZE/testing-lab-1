package org.example.testinglab1.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.enums.ProductCategory;
import org.example.testinglab1.enums.Readiness;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class ProductSummaryDto {
    private UUID id;
    private String name;
    private String photoUrl;
    private ProductCategory category;
}
