package org.example.testinglab1.dish;

import org.example.testinglab1.dto.request.CreateProductRequest;
import org.example.testinglab1.dto.response.DishFullDto;
import org.example.testinglab1.entity.Dish;
import org.example.testinglab1.entity.DishProduct;
import org.example.testinglab1.entity.Product;
import org.example.testinglab1.enums.DishCategory;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.enums.ProductCategory;
import org.example.testinglab1.enums.Readiness;
import org.example.testinglab1.repository.DishRepository;
import org.example.testinglab1.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class DishControllerDeleteTest {

    @Autowired
    private RestTestClient client;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanup() {
        dishRepository.deleteAll();
        productRepository.deleteAll();
    }

    private Product createProduct() {
        Product product = new Product();
        product.setName("Свекла");
        product.setCalories(88.0);
        product.setProteins(1.5);
        product.setFats(0.1);
        product.setCarbohydrates(8.8);
        product.setCategory(ProductCategory.VEGETABLES);
        product.setReadiness(Readiness.REQUIRES_COOKING);
        return productRepository.save(product);
    }

    private Dish createDish(Product product) {
        Dish dish = new Dish();
        dish.setName("Борщ");
        dish.setCalories(350.0);
        dish.setProteins(15.0);
        dish.setFats(10.0);
        dish.setCarbohydrates(40.0);
        dish.setPortionSize(200.0);
        dish.setCategory(DishCategory.SOUP);

        dish = dishRepository.save(dish);

        DishProduct ingredient = new DishProduct();
        ingredient.setId(new DishProduct.DishProductId(dish.getId(), product.getId()));
        ingredient.setDish(dish);
        ingredient.setProduct(product);
        ingredient.setAmount(150.0);
        dish.getIngredients().add(ingredient);

        return dishRepository.save(dish);
    }

    @Test
    @DisplayName("Валидный id, блюдо существует, должен удалить и вернуть 200")
    void test1() {
        Product product = createProduct();
        Dish saved = createDish(product);

        client.delete()
                .uri("/dishes/" + saved.getId())
                .exchange()
                .expectStatus().isOk();

        assertThat(dishRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Валидный id, блюдо не существует, должен вернуть 200")
    void test2() {
        client.delete()
                .uri("/dishes/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isOk();
    }

    @ParameterizedTest
    @DisplayName("Невалидные id, должен вернуть 400")
    @ValueSource(strings = {
            "not-a-uuid",
            "123",
            "abc-def",
            "!@#$%^&*()",
            "550e8400-e29b-41d4-a716-4466554400001",
            "550e8400-e29b-41d4-a716",
            "550e8400-e29b-41d4-a716-446655440000-1"
    })
    void test3(String id) {
        client.delete()
                .uri("/dishes/" + id)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @DisplayName("Граничные валидные UUID, блюдо не существует, должен вернуть 200")
    @ValueSource(strings = {
            "00000000-0000-0000-0000-000000000000",
            "ffffffff-ffff-ffff-ffff-ffffffffffff"
    })
    void test4(String id) {
        client.delete()
                .uri("/dishes/" + id)
                .exchange()
                .expectStatus().isOk();
    }
}
