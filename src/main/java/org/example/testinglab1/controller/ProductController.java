package org.example.testinglab1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.testinglab1.dto.filter.ProductFilter;
import org.example.testinglab1.dto.request.CreateProductRequest;
import org.example.testinglab1.dto.request.UpdateProductRequest;
import org.example.testinglab1.dto.response.ProductFullDto;
import org.example.testinglab1.dto.response.ProductPageDto;
import org.example.testinglab1.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductPageDto> getAll(
            Pageable pageable,
            ProductFilter filter
    ){
        log.info("GET /products - filter: {}, pageable: {}", filter, pageable);
        return ResponseEntity.ok(productService.getAll(pageable, filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductFullDto> getById(@PathVariable UUID id){
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody CreateProductRequest request){
        return ResponseEntity.ok(productService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateById(
            @Valid @RequestBody UpdateProductRequest request,
            @PathVariable UUID id
    ){
        productService.updateById(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable UUID id
    ){
        productService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
