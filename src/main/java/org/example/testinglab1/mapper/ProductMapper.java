package org.example.testinglab1.mapper;

import org.example.testinglab1.dto.request.CreateProductRequest;
import org.example.testinglab1.dto.response.ProductFullDto;
import org.example.testinglab1.dto.response.ProductPageDto;
import org.example.testinglab1.dto.response.ProductSummaryDto;
import org.example.testinglab1.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toEntity(CreateProductRequest request){
        Product product = new Product();

        product.setName(request.getName());
        product.setPhotos(request.getPhotos());
        product.setCalories(request.getCalories());
        product.setProteins(request.getProteins());
        product.setFats(request.getFats());
        product.setCarbohydrates(request.getCarbohydrates());
        product.setComposition(request.getComposition());
        product.setCategory(request.getCategory());
        product.setReadiness(request.getReadiness());
        product.setFlags(request.getFlags());

        return product;
    }

    public ProductFullDto toFullDto(Product product){
        return ProductFullDto.builder()
                .id(product.getId())
                .name(product.getName())
                .photos(product.getPhotos())
                .calories(product.getCalories())
                .proteins(product.getProteins())
                .fats(product.getFats())
                .carbohydrates(product.getCarbohydrates())
                .composition(product.getComposition())
                .category(product.getCategory())
                .readiness(product.getReadiness())
                .flags(product.getFlags())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductSummaryDto toSummaryDto(Product product){
        return ProductSummaryDto.builder()
                .id(product.getId())
                .name(product.getName())
                .photoUrl(product.getPhotos().isEmpty() ? "" : product.getPhotos().get(0))
                .category(product.getCategory())
                .build();
    }

    public ProductPageDto toPageDto(Page<Product> page){
        Page<ProductSummaryDto> page2 = page.map(product -> toSummaryDto(product));

        return ProductPageDto.builder()
                .content(page2.getContent())
                .page(page2.getNumber())
                .size(page2.getSize())
                .totalElements(page2.getTotalElements())
                .build();
    }
}
