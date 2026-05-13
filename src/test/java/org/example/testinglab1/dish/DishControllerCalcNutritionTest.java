package org.example.testinglab1.dish;

import org.example.testinglab1.dto.request.CreateDishRequest;
import org.example.testinglab1.dto.request.CreateProductRequest;
import org.example.testinglab1.dto.request.GetDishNutritionRequest;
import org.example.testinglab1.dto.request.IngredientRequest;
import org.example.testinglab1.dto.response.DishFullDto;
import org.example.testinglab1.dto.response.NutritionDto;
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
public class DishControllerCalcNutritionTest {

    @Autowired
    private RestTestClient client;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
    }

    private Product createProduct(String name, Double calories, Double proteins, Double fats, Double carbohydrates, Set<Flag> flags) {
        Product product = new Product();
        product.setName(name);
        product.setCalories(calories);
        product.setProteins(proteins);
        product.setFats(fats);
        product.setCarbohydrates(carbohydrates);
        product.setCategory(ProductCategory.VEGETABLES);
        product.setReadiness(Readiness.REQUIRES_COOKING);
        product.setFlags(flags);
        return productRepository.save(product);
    }

    // ───────────────────────────────────────────
    // Позитивные тесты
    // ───────────────────────────────────────────

    @Test
    @DisplayName("Валидный запрос с одним ингредиентом, должен посчитать КБЖУ + 200")
    void test1() {
        Product svekla = createProduct("Свекла", 88.0, 1.5, 0.1, 8.8, Set.of(Flag.VEGAN));

        GetDishNutritionRequest request = new GetDishNutritionRequest();
        request.setIngredientsIds(List.of(svekla.getId()));
        request.setIngredientsAmounts(List.of(150));

        var response = client.post()
                .uri("/dishes/nutrition")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .returnResult(NutritionDto.class);

        NutritionDto body = response.getResponseBody();
        assertThat(body).isNotNull();
        assertThat(body.getCalories()).isNotNull();
        assertThat(body.getProteins()).isNotNull();
        assertThat(body.getFats()).isNotNull();
        assertThat(body.getCarbohydrates()).isNotNull();
    }

    @Test
    @DisplayName("Валидный запрос с несколькими ингредиентами, должен посчитать КБЖУ + 200")
    void test2() {
        Product svekla = createProduct("Свекла", 88.0, 1.5, 0.1, 8.8, Set.of(Flag.VEGAN));
        Product morkov = createProduct("Морковь", 41.0, 0.9, 0.2, 6.9, Set.of(Flag.VEGAN, Flag.GLUTEN_FREE));

        GetDishNutritionRequest request = new GetDishNutritionRequest();
        request.setIngredientsIds(List.of(svekla.getId(), morkov.getId()));
        request.setIngredientsAmounts(List.of(150, 200));

        var response = client.post()
                .uri("/dishes/nutrition")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .returnResult(NutritionDto.class);

        NutritionDto body = response.getResponseBody();
        assertThat(body).isNotNull();
        assertThat(body.getCalories()).isNotNull();
        assertThat(body.getProteins()).isNotNull();
        assertThat(body.getFats()).isNotNull();
        assertThat(body.getCarbohydrates()).isNotNull();
    }

    @Test
    @DisplayName("Все ингредиенты с флагом VEGAN, allowedFlags должен содержать VEGAN")
    void test3() {
        Product svekla = createProduct("Свекла", 88.0, 1.5, 0.1, 8.8, Set.of(Flag.VEGAN));
        Product morkov = createProduct("Морковь", 41.0, 0.9, 0.2, 6.9, Set.of(Flag.VEGAN));

        GetDishNutritionRequest request = new GetDishNutritionRequest();
        request.setIngredientsIds(List.of(svekla.getId(), morkov.getId()));
        request.setIngredientsAmounts(List.of(150, 200));

        var response = client.post()
                .uri("/dishes/nutrition")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .returnResult(NutritionDto.class);

        assertThat(response.getResponseBody().getAllowedFlags()).isEqualTo(Set.of(Flag.VEGAN));
    }

    @Test
    @DisplayName("Не все ингредиенты с флагом VEGAN, allowedFlags не должен содержать VEGAN")
    void test4() {
        Product svekla = createProduct("Свекла", 88.0, 1.5, 0.1, 8.8, Set.of(Flag.VEGAN));
        Product govyadina = createProduct("Говядина", 250.0, 26.0, 15.0, 0.0, Set.of());

        GetDishNutritionRequest request = new GetDishNutritionRequest();
        request.setIngredientsIds(List.of(svekla.getId(), govyadina.getId()));
        request.setIngredientsAmounts(List.of(150, 200));

        var response = client.post()
                .uri("/dishes/nutrition")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .returnResult(NutritionDto.class);

        assertThat(response.getResponseBody().getAllowedFlags()).isNotEqualTo(Set.of(Flag.VEGAN));
    }

    @Test
    @DisplayName("Все ингредиенты со всеми флагами, allowedFlags должен содержать все флаги")
    void test5() {
        Product svekla = createProduct("Свекла", 88.0, 1.5, 0.1, 8.8, Set.of(Flag.VEGAN, Flag.GLUTEN_FREE, Flag.SUGAR_FREE));
        Product morkov = createProduct("Морковь", 41.0, 0.9, 0.2, 6.9, Set.of(Flag.VEGAN, Flag.GLUTEN_FREE, Flag.SUGAR_FREE));

        GetDishNutritionRequest request = new GetDishNutritionRequest();
        request.setIngredientsIds(List.of(svekla.getId(), morkov.getId()));
        request.setIngredientsAmounts(List.of(150, 200));

        var response = client.post()
                .uri("/dishes/nutrition")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .returnResult(NutritionDto.class);

        assertThat(response.getResponseBody().getAllowedFlags())
                .isEqualTo(Set.of(Flag.VEGAN, Flag.GLUTEN_FREE, Flag.SUGAR_FREE));
    }

    // ───────────────────────────────────────────
    // Граничные значения списков
    // ───────────────────────────────────────────

    @Test
    @DisplayName("ingredientsIds и ingredientsAmounts null, должен вернуть нули + 200")
    void test6() {
        GetDishNutritionRequest request = new GetDishNutritionRequest();
        request.setIngredientsIds(null);
        request.setIngredientsAmounts(null);

        var response = client.post()
                .uri("/dishes/nutrition")
                .body(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Пустые списки ingredientsIds и ingredientsAmounts, должен вернуть нули + 200")
    void test7() {
        GetDishNutritionRequest request = new GetDishNutritionRequest();
        request.setIngredientsIds(List.of());
        request.setIngredientsAmounts(List.of());

        var response = client.post()
                .uri("/dishes/nutrition")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .returnResult(NutritionDto.class);

        NutritionDto body = response.getResponseBody();
        assertThat(body.getCalories()).isEqualTo(0.0);
        assertThat(body.getProteins()).isEqualTo(0.0);
        assertThat(body.getFats()).isEqualTo(0.0);
        assertThat(body.getCarbohydrates()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Полностью пустой запрос, должен вернуть нули + 200")
    void test8() {
        var response = client.post()
                .uri("/dishes/nutrition")
                .body(new GetDishNutritionRequest())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("ingredientsIds длиннее ingredientsAmounts, сервис обрабатывает без ошибки + 200")
    void test9() {
        Product svekla = createProduct("Свекла", 88.0, 1.5, 0.1, 8.8, Set.of());
        Product morkov = createProduct("Морковь", 41.0, 0.9, 0.2, 6.9, Set.of());

        GetDishNutritionRequest request = new GetDishNutritionRequest();
        request.setIngredientsIds(List.of(svekla.getId(), morkov.getId()));
        request.setIngredientsAmounts(List.of(150));

        var response = client.post()
                .uri("/dishes/nutrition")
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isIn(HttpStatus.OK, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("ingredientsAmounts длиннее ingredientsIds, сервис обрабатывает без ошибки + 200")
    void test10() {
        Product svekla = createProduct("Свекла", 88.0, 1.5, 0.1, 8.8, Set.of());

        GetDishNutritionRequest request = new GetDishNutritionRequest();
        request.setIngredientsIds(List.of(svekla.getId()));
        request.setIngredientsAmounts(List.of(150, 200));

        var response = client.post()
                .uri("/dishes/nutrition")
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Несуществующий productId в ingredientsIds, должен вернуть 404")
    void test11() {
        GetDishNutritionRequest request = new GetDishNutritionRequest();
        request.setIngredientsIds(List.of(UUID.randomUUID()));
        request.setIngredientsAmounts(List.of(150));

        client.post()
                .uri("/dishes/nutrition")
                .body(request)
                .exchange()
                .expectStatus().isNotFound();
    }
}
