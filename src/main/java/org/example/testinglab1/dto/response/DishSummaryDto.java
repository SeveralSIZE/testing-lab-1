package org.example.testinglab1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.testinglab1.enums.DishCategory;
import org.example.testinglab1.enums.Flag;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishSummaryDto {
    private UUID id;
    private String name;
    private String photoUrl;
    private DishCategory category;
}
