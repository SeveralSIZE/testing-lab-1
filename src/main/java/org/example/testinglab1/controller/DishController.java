package org.example.testinglab1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.testinglab1.dto.filter.DishFilter;
import org.example.testinglab1.dto.request.CreateDishRequest;
import org.example.testinglab1.dto.request.GetDishNutritionRequest;
import org.example.testinglab1.dto.request.UpdateDishRequest;
import org.example.testinglab1.dto.response.DishFullDto;
import org.example.testinglab1.dto.response.DishPageDto;
import org.example.testinglab1.dto.response.NutritionDto;
import org.example.testinglab1.service.DishService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;

    @GetMapping
    public ResponseEntity<DishPageDto> getAll(
            Pageable pageable,
            DishFilter filter
    ) {
        return ResponseEntity.ok(dishService.getAll(pageable, filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishFullDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(dishService.getById(id));
    }

    @PostMapping("/nutrition")
    public ResponseEntity<NutritionDto> calcNutrition(@Valid @RequestBody GetDishNutritionRequest request){
        return ResponseEntity.ok(dishService.calcNutrition(request));
    }

    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody CreateDishRequest request) {
        return ResponseEntity.ok(dishService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateById(
            @Valid @RequestBody UpdateDishRequest request,
            @PathVariable UUID id
    ) {
        dishService.updateById(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        dishService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}