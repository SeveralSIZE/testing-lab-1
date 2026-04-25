package org.example.testinglab1.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DishPageDto {
    private List<DishSummaryDto> content;
    private int page;
    private int size;
    private long totalElements;
}
