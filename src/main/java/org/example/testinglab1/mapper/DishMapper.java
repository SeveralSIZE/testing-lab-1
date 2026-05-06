package org.example.testinglab1.mapper;

import org.example.testinglab1.dto.request.CreateDishRequest;
import org.example.testinglab1.dto.response.DishFullDto;
import org.example.testinglab1.dto.response.DishPageDto;
import org.example.testinglab1.dto.response.DishProductDto;
import org.example.testinglab1.dto.response.DishSummaryDto;
import org.example.testinglab1.entity.Dish;
import org.example.testinglab1.entity.DishProduct;
import org.example.testinglab1.enums.DishCategory;
import org.example.testinglab1.enums.Flag;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DishMapper {
    public Dish toEntity(CreateDishRequest request,
                         String resolvedName,
                         DishCategory resolvedCategory,
                         Double calories,
                         Double proteins,
                         Double fats,
                         Double carbohydrates,
                         Set<Flag> flags) {
        Dish dish = new Dish();
        dish.setName(resolvedName);
        dish.setPhotos(request.getPhotos());
        dish.setCalories(calories);
        dish.setProteins(proteins);
        dish.setFats(fats);
        dish.setCarbohydrates(carbohydrates);
        dish.setPortionSize(request.getPortionSize());
        dish.setCategory(resolvedCategory);
        dish.setFlags(flags);
        return dish;
    }

    public DishFullDto toFullDto(Dish dish) {
        return DishFullDto.builder()
                .id(dish.getId())
                .name(dish.getName())
                .photos(dish.getPhotos())
                .calories(dish.getCalories())
                .proteins(dish.getProteins())
                .fats(dish.getFats())
                .carbohydrates(dish.getCarbohydrates())
                .portionSize(dish.getPortionSize())
                .category(dish.getCategory())
                .flags(dish.getFlags())
                .ingredients(dish.getIngredients().stream()
                        .map(this::toDishProductDto)
                        .toList())
                .createdAt(dish.getCreatedAt())
                .updatedAt(dish.getUpdatedAt())
                .build();
    }

    public DishSummaryDto toSummaryDto(Dish dish) {
        return DishSummaryDto.builder()
                .id(dish.getId())
                .name(dish.getName())
                .photoUrl(dish.getPhotos() != null && !dish.getPhotos().isEmpty()
                        ? dish.getPhotos().get(0) : null)
                .category(dish.getCategory())
                .build();
    }

    public DishPageDto toPageDto(Page<Dish> page) {
        Page<DishSummaryDto> mapped = page.map(this::toSummaryDto);
        return DishPageDto.builder()
                .content(mapped.getContent())
                .page(mapped.getNumber())
                .size(mapped.getSize())
                .totalElements(mapped.getTotalElements())
                .build();
    }

    private DishProductDto toDishProductDto(DishProduct dp) {
        return DishProductDto.builder()
                .productId(dp.getProduct().getId())
                .productName(dp.getProduct().getName())
                .amount(dp.getAmount())
                .build();
    }
}
