package org.example.testinglab1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageDto {
    private List<ProductSummaryDto> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
