package org.example.testinglab1.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProductPageDto {
    private List<ProductSummaryDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
