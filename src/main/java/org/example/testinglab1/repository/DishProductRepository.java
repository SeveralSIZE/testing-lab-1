package org.example.testinglab1.repository;

import org.example.testinglab1.entity.DishProduct;
import org.example.testinglab1.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DishProductRepository extends JpaRepository<DishProduct, UUID> {
}
