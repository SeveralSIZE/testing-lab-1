package org.example.testinglab1.product;

import org.example.testinglab1.entity.Product;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.enums.ProductCategory;
import org.example.testinglab1.enums.Readiness;
import org.example.testinglab1.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class ProductControllerDeleteTest {

    @Autowired
    private RestTestClient client;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
    }

    private Product createProduct() {
        Product product = new Product();
        product.setName("Свекла");
        product.setCalories(88.0);
        product.setProteins(1.5);
        product.setFats(0.1);
        product.setCarbohydrates(8.8);
        product.setComposition("Корнеплод");
        product.setCategory(ProductCategory.VEGETABLES);
        product.setReadiness(Readiness.REQUIRES_COOKING);
        product.setFlags(Set.of(Flag.VEGAN));
        product.setPhotos(List.of("svekla.png"));
        return productRepository.save(product);
    }

    @Test
    @DisplayName("Валидный id, продукт существует, должен удалить и вернуть 200")
    void test1() {
        Product saved = createProduct();

        client.delete()
                .uri("/products/" + saved.getId())
                .exchange()
                .expectStatus().isOk();

        assertThat(productRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Валидный id, продукт не существует, должен вернуть 200")
    void test2() {
        client.delete()
                .uri("/products/" + UUID.randomUUID())
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
                .uri("/products/" + id)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Удаление уже удаленного продукта, должен вернуть 200")
    void test4() {
        Product saved = createProduct();

        client.delete()
                .uri("/products/" + saved.getId())
                .exchange()
                .expectStatus().isOk();

        client.delete()
                .uri("/products/" + saved.getId())
                .exchange()
                .expectStatus().isOk();

        assertThat(productRepository.findById(saved.getId())).isEmpty();
    }
}
