package org.example.testinglab1.service;

import org.example.testinglab1.dto.filter.ProductFilter;
import org.example.testinglab1.dto.request.CreateProductRequest;
import org.example.testinglab1.dto.request.UpdateProductRequest;
import org.example.testinglab1.dto.response.ProductFullDto;
import org.example.testinglab1.dto.response.ProductPageDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {
    UUID create(CreateProductRequest request);
    ProductPageDto getAll(Pageable pageable, ProductFilter filter);
    ProductFullDto getById(UUID id);
    void deleteById(UUID id);
    void updateById(UUID id, UpdateProductRequest request);
}
