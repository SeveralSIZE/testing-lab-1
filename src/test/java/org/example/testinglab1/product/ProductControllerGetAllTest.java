package org.example.testinglab1.product;

import org.example.testinglab1.dto.response.ProductPageDto;
import org.example.testinglab1.entity.Product;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.enums.ProductCategory;
import org.example.testinglab1.enums.Readiness;
import org.example.testinglab1.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class ProductControllerGetAllTest {

    @Autowired
    private RestTestClient client;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
    }

    private Product createProduct(String name, ProductCategory category, Readiness readiness, Set<Flag> flags) {
        Product product = new Product();
        product.setName(name);
        product.setCalories(88.0);
        product.setProteins(1.5);
        product.setFats(0.1);
        product.setCarbohydrates(8.8);
        product.setCategory(category);
        product.setReadiness(readiness);
        product.setFlags(flags);
        return productRepository.save(product);
    }

    // ───────────────────────────────────────────
    // Позитивные тесты
    // ───────────────────────────────────────────

    @Test
    @DisplayName("Без фильтров, должен вернуть все продукты + 200")
    void test1() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));
        createProduct("Говядина", ProductCategory.MEAT, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        ProductPageDto body = response.getResponseBody();
        assertThat(body).isNotNull();
        assertThat(body.getTotalElements()).isEqualTo(3);
        assertThat(body.getContent()).isNotNull();
    }

    @Test
    @DisplayName("Без фильтров, пустая база, должен вернуть пустой список + 200")
    void test2() {
        var response = client.get()
                .uri("/products")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        ProductPageDto body = response.getResponseBody();
        assertThat(body).isNotNull();
        assertThat(body.getTotalElements()).isEqualTo(0);
        assertThat(body.getContent()).isNotNull();
    }

    // ───────────────────────────────────────────
    // Фильтр по name
    // ───────────────────────────────────────────

    @Test
    @DisplayName("Фильтр по name, точное совпадение, должен вернуть один продукт")
    void test3() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?name=Свекла")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Фильтр по name, частичное совпадение, должен вернуть подходящие продукты")
    void test4() {
        createProduct("Свекла красная", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Свекла белая", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?name=Свекла")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по name в нижнем регистре, должен вернуть совпадения (case-insensitive)")
    void test5() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?name=свекла")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Фильтр по name, нет совпадений, должен вернуть пустой список")
    void test6() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?name=Картофель")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Фильтр по name, пустая строка, должен вернуть все продукты")
    void test7() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?name=")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по name, только пробелы, должен вернуть все продукты")
    void test8() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?name=   ")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    // ───────────────────────────────────────────
    // Фильтр по categories
    // ───────────────────────────────────────────

    @Test
    @DisplayName("Фильтр по одной category, должен вернуть только продукты этой категории")
    void test9() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Говядина", ProductCategory.MEAT, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?categories=VEGETABLES")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по нескольким categories, должен вернуть продукты всех указанных категорий")
    void test10() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Говядина", ProductCategory.MEAT, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Мороженое", ProductCategory.FROZEN, Readiness.READY_TO_EAT, Set.of());

        var response = client.get()
                .uri("/products?categories=VEGETABLES&categories=MEAT")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по category, нет совпадений, должен вернуть пустой список")
    void test11() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?categories=MEAT")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(0);
    }

    // ───────────────────────────────────────────
    // Фильтр по readiness
    // ───────────────────────────────────────────

    @Test
    @DisplayName("Фильтр по одному readiness, должен вернуть только подходящие продукты")
    void test12() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Салат", ProductCategory.VEGETABLES, Readiness.READY_TO_EAT, Set.of());
        createProduct("Фарш", ProductCategory.MEAT, Readiness.SEMI_FINISHED, Set.of());

        var response = client.get()
                .uri("/products?readiness=REQUIRES_COOKING")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Фильтр по нескольким readiness, должен вернуть продукты всех указанных статусов")
    void test13() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Салат", ProductCategory.VEGETABLES, Readiness.READY_TO_EAT, Set.of());
        createProduct("Фарш", ProductCategory.MEAT, Readiness.SEMI_FINISHED, Set.of());

        var response = client.get()
                .uri("/products?readiness=REQUIRES_COOKING&readiness=READY_TO_EAT")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по readiness, нет совпадений, должен вернуть пустой список")
    void test14() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?readiness=READY_TO_EAT")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(0);
    }

    // ───────────────────────────────────────────
    // Фильтр по flags
    // ───────────────────────────────────────────

    @Test
    @DisplayName("Фильтр по одному flag, должен вернуть только продукты с этим флагом")
    void test15() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN, Flag.GLUTEN_FREE));
        createProduct("Говядина", ProductCategory.MEAT, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?flags=VEGAN")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по нескольким flags, должен вернуть продукты у которых есть все указанные флаги")
    void test16() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN, Flag.GLUTEN_FREE));
        createProduct("Говядина", ProductCategory.MEAT, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?flags=VEGAN&flags=GLUTEN_FREE")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Фильтр по flag, нет совпадений, должен вернуть пустой список")
    void test17() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));

        var response = client.get()
                .uri("/products?flags=SUGAR_FREE")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(0);
    }

    // ───────────────────────────────────────────
    // Комбинированные фильтры
    // ───────────────────────────────────────────

    @Test
    @DisplayName("Фильтр по name + category, должен вернуть только совпадающие продукты")
    void test18() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Свекла маринованная", ProductCategory.CANNED, Readiness.READY_TO_EAT, Set.of());
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?name=Свекла&categories=VEGETABLES")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Фильтр по category + readiness, должен вернуть только совпадающие продукты")
    void test19() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Салат", ProductCategory.VEGETABLES, Readiness.READY_TO_EAT, Set.of());
        createProduct("Говядина", ProductCategory.MEAT, Readiness.REQUIRES_COOKING, Set.of());

        var response = client.get()
                .uri("/products?categories=VEGETABLES&readiness=REQUIRES_COOKING")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Фильтр по category + flags, должен вернуть только совпадающие продукты")
    void test20() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of());
        createProduct("Говядина", ProductCategory.MEAT, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));

        var response = client.get()
                .uri("/products?categories=VEGETABLES&flags=VEGAN")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Все фильтры вместе, должен вернуть только полностью совпадающие продукты")
    void test21() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.READY_TO_EAT, Set.of(Flag.VEGAN));
        createProduct("Свекла", ProductCategory.MEAT, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));
        createProduct("Морковь", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));

        var response = client.get()
                .uri("/products?name=Свекла&categories=VEGETABLES&readiness=REQUIRES_COOKING&flags=VEGAN")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Все фильтры вместе, нет совпадений, должен вернуть пустой список")
    void test22() {
        createProduct("Свекла", ProductCategory.VEGETABLES, Readiness.REQUIRES_COOKING, Set.of(Flag.VEGAN));

        var response = client.get()
                .uri("/products?name=Морковь&categories=VEGETABLES&readiness=REQUIRES_COOKING&flags=VEGAN")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(0);
    }
}
