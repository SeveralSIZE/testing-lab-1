package org.example.testinglab1.dish;

import org.example.testinglab1.dto.request.CreateDishRequest;
import org.example.testinglab1.dto.request.CreateProductRequest;
import org.example.testinglab1.dto.request.IngredientRequest;
import org.example.testinglab1.dto.request.UpdateDishRequest;
import org.example.testinglab1.dto.response.DishFullDto;
import org.example.testinglab1.dto.response.DishPageDto;
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
public class DishControllerGetAllTest {

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

    private Dish createDish(String name, DishCategory category, Set<Flag> flags) {
        Dish dish = new Dish();
        dish.setName(name);
        dish.setCalories(350.0);
        dish.setProteins(15.0);
        dish.setFats(10.0);
        dish.setCarbohydrates(40.0);
        dish.setPortionSize(200.0);
        dish.setCategory(category);
        dish.setFlags(flags);
        return dishRepository.save(dish);
    }

    @Test
    @DisplayName("Без фильтров, должен вернуть все блюда + 200")
    void test1() {
        createDish("Борщ", DishCategory.SOUP, Set.of(Flag.VEGAN));
        createDish("Салат Цезарь", DishCategory.SALAD, Set.of());
        createDish("Тирамису", DishCategory.DESSERT, Set.of());

        var response = client.get()
                .uri("/dishes")
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("Без фильтров, пустая база, должен вернуть пустой список + 200")
    void test2() {
        var response = client.get()
                .uri("/dishes")
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Фильтр по name, точное совпадение, должен вернуть один результат")
    void test3() {
        createDish("Борщ", DishCategory.SOUP, Set.of());
        createDish("Солянка", DishCategory.SOUP, Set.of());

        var response = client.get()
                .uri(builder -> builder.path("/dishes").queryParam("name", "Борщ").build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Фильтр по name, частичное совпадение, должен вернуть подходящие блюда")
    void test4() {
        createDish("Борщ красный", DishCategory.SOUP, Set.of());
        createDish("Борщ зелёный", DishCategory.SOUP, Set.of());
        createDish("Солянка", DishCategory.SOUP, Set.of());

        var response = client.get()
                .uri(builder -> builder.path("/dishes").queryParam("name", "Борщ").build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по name, нет совпадений, должен вернуть пустой список")
    void test5() {
        createDish("Борщ", DishCategory.SOUP, Set.of());

        var response = client.get()
                .uri(builder -> builder.path("/dishes").queryParam("name", "Пицца").build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Фильтр по name, пустая строка, должен вернуть все блюда")
    void test6() {
        createDish("Борщ", DishCategory.SOUP, Set.of());
        createDish("Салат", DishCategory.SALAD, Set.of());

        var response = client.get()
                .uri("/dishes?name=")
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по одной category, должен вернуть только блюда этой категории")
    void test7() {
        createDish("Борщ", DishCategory.SOUP, Set.of());
        createDish("Солянка", DishCategory.SOUP, Set.of());
        createDish("Тирамису", DishCategory.DESSERT, Set.of());

        var response = client.get()
                .uri("/dishes?categories=SOUP")
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по нескольким categories, должен вернуть блюда всех указанных категорий")
    void test8() {
        createDish("Борщ", DishCategory.SOUP, Set.of());
        createDish("Цезарь", DishCategory.SALAD, Set.of());
        createDish("Тирамису", DishCategory.DESSERT, Set.of());

        var response = client.get()
                .uri("/dishes?categories=SOUP&categories=SALAD")
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по category, нет совпадений, должен вернуть пустой список")
    void test9() {
        createDish("Борщ", DishCategory.SOUP, Set.of());

        var response = client.get()
                .uri("/dishes?categories=DESSERT")
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Фильтр по одному flag, должен вернуть только блюда с этим флагом")
    void test10() {
        createDish("Борщ", DishCategory.SOUP, Set.of(Flag.VEGAN));
        createDish("Цезарь", DishCategory.SALAD, Set.of(Flag.VEGAN, Flag.GLUTEN_FREE));
        createDish("Тирамису", DishCategory.DESSERT, Set.of());

        var response = client.get()
                .uri("/dishes?flags=VEGAN")
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Фильтр по нескольким flags, должен вернуть блюда у которых есть все указанные флаги")
    void test11() {
        createDish("Борщ", DishCategory.SOUP, Set.of(Flag.VEGAN));
        createDish("Цезарь", DishCategory.SALAD, Set.of(Flag.VEGAN, Flag.GLUTEN_FREE));
        createDish("Тирамису", DishCategory.DESSERT, Set.of());

        var response = client.get()
                .uri("/dishes?flags=VEGAN&flags=GLUTEN_FREE")
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Фильтр по flag, нет совпадений, должен вернуть пустой список")
    void test12() {
        createDish("Борщ", DishCategory.SOUP, Set.of(Flag.VEGAN));

        var response = client.get()
                .uri("/dishes?flags=SUGAR_FREE")
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("Фильтр по name + category, должен вернуть только совпадающие блюда")
    void test13() {
        createDish("Борщ", DishCategory.SOUP, Set.of());
        createDish("Борщ холодный", DishCategory.SALAD, Set.of());
        createDish("Солянка", DishCategory.SOUP, Set.of());

        var response = client.get()
                .uri(builder -> builder.path("/dishes")
                        .queryParam("name", "Борщ")
                        .queryParam("categories", "SOUP")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Фильтр по category + flags, должен вернуть только совпадающие блюда")
    void test14() {
        createDish("Борщ", DishCategory.SOUP, Set.of(Flag.VEGAN));
        createDish("Солянка", DishCategory.SOUP, Set.of());
        createDish("Цезарь", DishCategory.SALAD, Set.of(Flag.VEGAN));

        var response = client.get()
                .uri("/dishes?categories=SOUP&flags=VEGAN")
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Все фильтры вместе, должен вернуть только полностью совпадающие блюда")
    void test15() {
        createDish("Борщ", DishCategory.SOUP, Set.of(Flag.VEGAN));
        createDish("Борщ", DishCategory.SOUP, Set.of());
        createDish("Борщ", DishCategory.SALAD, Set.of(Flag.VEGAN));
        createDish("Солянка", DishCategory.SOUP, Set.of(Flag.VEGAN));

        var response = client.get()
                .uri(builder -> builder.path("/dishes")
                        .queryParam("name", "Борщ")
                        .queryParam("categories", "SOUP")
                        .queryParam("flags", "VEGAN")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Все фильтры вместе, нет совпадений, должен вернуть пустой список")
    void test16() {
        createDish("Борщ", DishCategory.SOUP, Set.of(Flag.VEGAN));

        var response = client.get()
                .uri(builder -> builder.path("/dishes")
                        .queryParam("name", "Пицца")
                        .queryParam("categories", "SOUP")
                        .queryParam("flags", "VEGAN")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(DishPageDto.class);

        assertThat(response.getResponseBody().getTotalElements()).isEqualTo(0);
    }
}
