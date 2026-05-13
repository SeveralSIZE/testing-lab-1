package org.example.testinglab1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishPageDto {
    private List<DishSummaryDto> content;
    private int page;
    private int size;
    private long totalElements;
}
