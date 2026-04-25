package org.example.testinglab1.service;

import org.example.testinglab1.dto.filter.DishFilter;
import org.example.testinglab1.dto.request.CreateDishRequest;
import org.example.testinglab1.dto.request.UpdateDishRequest;
import org.example.testinglab1.dto.response.DishFullDto;
import org.example.testinglab1.dto.response.DishPageDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DishService {
    UUID create(CreateDishRequest request);
    DishPageDto getAll(Pageable pageable, DishFilter filter);
    DishFullDto getById(UUID id);
    void deleteById(UUID id);
    void updateById(UUID id, UpdateDishRequest request);
}
