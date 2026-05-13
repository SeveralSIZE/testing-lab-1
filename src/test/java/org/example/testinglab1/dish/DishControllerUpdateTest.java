package org.example.testinglab1.dish;

import org.example.testinglab1.dto.request.CreateDishRequest;
import org.example.testinglab1.dto.request.CreateProductRequest;
import org.example.testinglab1.dto.request.IngredientRequest;
import org.example.testinglab1.dto.request.UpdateDishRequest;
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
public class DishControllerUpdateTest {

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

    private UpdateDishRequest validRequest(UUID productId) {
        UpdateDishRequest request = new UpdateDishRequest();
        request.setName("Борщ обновлённый");
        request.setCalories(400.0);
        request.setProteins(20.0);
        request.setFats(15.0);
        request.setCarbohydrates(50.0);
        request.setPortionSize(250.0);
        request.setCategory(DishCategory.FIRST);
        request.setFlags(Set.of(Flag.VEGAN));
        request.setPhotos(List.of("borsch_new.png"));
        IngredientRequest ingredient = new IngredientRequest();
        ingredient.setProductId(productId);
        ingredient.setAmount(200.0);
        request.setIngredients(List.of(ingredient));
        return request;
    }

    @Test
    @DisplayName("Валидный запрос со всеми полями, блюдо существует, должен вернуть 200")
    void test1() {
        Product product = createProduct();
        Dish saved = createDish(product);

        client.patch()
                .uri("/dishes/" + saved.getId())
                .body(validRequest(product.getId()))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Полностью пустой запрос, блюдо существует, должен вернуть 200 (все поля необязательны)")
    void test2() {
        Product product = createProduct();
        Dish saved = createDish(product);

        client.patch()
                .uri("/dishes/" + saved.getId())
                .body(new UpdateDishRequest())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Валидный id, блюдо не существует, должен вернуть 404")
    void test3() {
        Product product = createProduct();

        client.patch()
                .uri("/dishes/" + UUID.randomUUID())
                .body(validRequest(product.getId()))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Несуществующий productId в ingredients, должен вернуть 404")
    void test4() {
        Product product = createProduct();
        Dish saved = createDish(product);
        UpdateDishRequest request = validRequest(UUID.randomUUID());

        client.patch()
                .uri("/dishes/" + saved.getId())
                .body(request)
                .exchange()
                .expectStatus().isNotFound();
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
    void test5(String id) {
        Product product = createProduct();

        client.patch()
                .uri("/dishes/" + id)
                .body(validRequest(product.getId()))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения name")
    @MethodSource("nameProvider")
    void test6(String name, HttpStatus expectedStatus) {
        Product product = createProduct();
        Dish saved = createDish(product);
        UpdateDishRequest request = validRequest(product.getId());
        request.setName(name);

        var response = client.patch()
                .uri("/dishes/" + saved.getId())
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения portionSize")
    @MethodSource("portionSizeProvider")
    void test7(Double portionSize, HttpStatus expectedStatus) {
        Product product = createProduct();
        Dish saved = createDish(product);
        UpdateDishRequest request = validRequest(product.getId());
        request.setPortionSize(portionSize);

        var response = client.patch()
                .uri("/dishes/" + saved.getId())
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения calories")
    @MethodSource("caloriesProvider")
    void test8(Double calories, HttpStatus expectedStatus) {
        Product product = createProduct();
        Dish saved = createDish(product);
        UpdateDishRequest request = validRequest(product.getId());
        request.setCalories(calories);

        var response = client.patch()
                .uri("/dishes/" + saved.getId())
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения proteins, fats, carbohydrates")
    @MethodSource("bjuProvider")
    void test9(Double proteins, Double fats, Double carbohydrates, HttpStatus expectedStatus) {
        Product product = createProduct();
        Dish saved = createDish(product);
        UpdateDishRequest request = validRequest(product.getId());
        request.setProteins(proteins);
        request.setFats(fats);
        request.setCarbohydrates(carbohydrates);

        var response = client.patch()
                .uri("/dishes/" + saved.getId())
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения photos")
    @MethodSource("photosProvider")
    void test10(List<String> photos, HttpStatus expectedStatus) {
        Product product = createProduct();
        Dish saved = createDish(product);
        UpdateDishRequest request = validRequest(product.getId());
        request.setPhotos(photos);

        var response = client.patch()
                .uri("/dishes/" + saved.getId())
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все валидные и невалидные значения category")
    @MethodSource("categoryProvider")
    void test11(DishCategory category, HttpStatus expectedStatus) {
        Product product = createProduct();
        Dish saved = createDish(product);
        UpdateDishRequest request = validRequest(product.getId());
        request.setCategory(category);

        var response = client.patch()
                .uri("/dishes/" + saved.getId())
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все валидные значения flags")
    @MethodSource("flagsProvider")
    void test12(Set<Flag> flags, HttpStatus expectedStatus) {
        Product product = createProduct();
        Dish saved = createDish(product);
        UpdateDishRequest request = validRequest(product.getId());
        request.setFlags(flags);

        var response = client.patch()
                .uri("/dishes/" + saved.getId())
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения ingredients")
    @MethodSource("ingredientsProvider")
    void test13(List<IngredientRequest> ingredients, HttpStatus expectedStatus) {
        Product product = createProduct();
        Dish saved = createDish(product);
        UpdateDishRequest request = validRequest(product.getId());
        request.setIngredients(ingredients);

        var response = client.patch()
                .uri("/dishes/" + saved.getId())
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    static Stream<Arguments> nameProvider() {
        return Stream.of(
                Arguments.of("Борщ",          HttpStatus.OK),
                Arguments.of("Бо",            HttpStatus.OK),
                Arguments.of("Бор",           HttpStatus.OK),
                Arguments.of("Б".repeat(254), HttpStatus.OK),
                Arguments.of("Б".repeat(255), HttpStatus.OK),
                Arguments.of((Object) null,   HttpStatus.OK),
                Arguments.of("Б",             HttpStatus.BAD_REQUEST),
                Arguments.of("Б".repeat(256), HttpStatus.BAD_REQUEST),
                Arguments.of("",              HttpStatus.BAD_REQUEST),
                Arguments.of(" ",             HttpStatus.BAD_REQUEST)
        );
    }

    static Stream<Arguments> portionSizeProvider() {
        return Stream.of(
                Arguments.of(200.0,         HttpStatus.OK),
                Arguments.of(0.1,           HttpStatus.OK),
                Arguments.of(1000.0,        HttpStatus.OK),
                Arguments.of((Object) null, HttpStatus.OK),
                Arguments.of(0.0,           HttpStatus.BAD_REQUEST),
                Arguments.of(-0.1,          HttpStatus.BAD_REQUEST),
                Arguments.of(-1.0,          HttpStatus.BAD_REQUEST)
        );
    }

    static Stream<Arguments> caloriesProvider() {
        return Stream.of(
                Arguments.of(350.0,         HttpStatus.OK),
                Arguments.of(0.0,           HttpStatus.OK),
                Arguments.of(0.1,           HttpStatus.OK),
                Arguments.of(1000000.0,     HttpStatus.OK),
                Arguments.of((Object) null, HttpStatus.OK),
                Arguments.of(-0.1,          HttpStatus.BAD_REQUEST),
                Arguments.of(-1.0,          HttpStatus.BAD_REQUEST)
        );
    }

    static Stream<Arguments> bjuProvider() {
        return Stream.of(
                Arguments.of(15.0,  10.0,  40.0,  HttpStatus.OK),
                Arguments.of(0.0,   0.0,   0.0,   HttpStatus.OK),
                Arguments.of(0.1,   0.1,   0.1,   HttpStatus.OK),
                Arguments.of(200.0, 200.0, 200.0, HttpStatus.OK),
                Arguments.of(null,  null,  null,  HttpStatus.OK),
                Arguments.of(-0.1,  0.0,   0.0,   HttpStatus.BAD_REQUEST),
                Arguments.of(0.0,   -0.1,  0.0,   HttpStatus.BAD_REQUEST),
                Arguments.of(0.0,   0.0,   -0.1,  HttpStatus.BAD_REQUEST)
        );
    }

    static Stream<Arguments> photosProvider() {
        return Stream.of(
                Arguments.of((Object) null,                                                              HttpStatus.OK),
                Arguments.of(List.of(),                                                                  HttpStatus.OK),
                Arguments.of(List.of("p1.png"),                                                          HttpStatus.OK),
                Arguments.of(List.of("p1.png", "p2.png", "p3.png", "p4.png"),                           HttpStatus.OK),
                Arguments.of(List.of("p1.png", "p2.png", "p3.png", "p4.png", "p5.png"),                 HttpStatus.OK),
                Arguments.of(List.of("p1.png", "p2.png", "p3.png", "p4.png", "p5.png", "p6.png"),       HttpStatus.BAD_REQUEST)
        );
    }

    static Stream<Arguments> categoryProvider() {
        return Stream.of(
                Arguments.of(DishCategory.DESSERT,  HttpStatus.OK),
                Arguments.of(DishCategory.FIRST,    HttpStatus.OK),
                Arguments.of(DishCategory.SECOND,   HttpStatus.OK),
                Arguments.of(DishCategory.DRINK,    HttpStatus.OK),
                Arguments.of(DishCategory.SALAD,    HttpStatus.OK),
                Arguments.of(DishCategory.SOUP,     HttpStatus.OK),
                Arguments.of(DishCategory.SNACK,    HttpStatus.OK),
                Arguments.of((Object) null,         HttpStatus.OK)
        );
    }

    static Stream<Arguments> flagsProvider() {
        return Stream.of(
                Arguments.of((Object) null,                                         HttpStatus.OK),
                Arguments.of(Set.of(),                                              HttpStatus.OK),
                Arguments.of(Set.of(Flag.VEGAN),                                    HttpStatus.OK),
                Arguments.of(Set.of(Flag.GLUTEN_FREE),                              HttpStatus.OK),
                Arguments.of(Set.of(Flag.SUGAR_FREE),                               HttpStatus.OK),
                Arguments.of(Set.of(Flag.VEGAN, Flag.GLUTEN_FREE),                  HttpStatus.OK),
                Arguments.of(Set.of(Flag.VEGAN, Flag.GLUTEN_FREE, Flag.SUGAR_FREE), HttpStatus.OK)
        );
    }

    static Stream<Arguments> ingredientsProvider() {
        IngredientRequest nullProductId = new IngredientRequest();
        nullProductId.setProductId(null);
        nullProductId.setAmount(150.0);

        IngredientRequest nullAmount = new IngredientRequest();
        nullAmount.setProductId(UUID.randomUUID());
        nullAmount.setAmount(null);

        IngredientRequest zeroAmount = new IngredientRequest();
        zeroAmount.setProductId(UUID.randomUUID());
        zeroAmount.setAmount(0.0);

        IngredientRequest negativeAmount = new IngredientRequest();
        negativeAmount.setProductId(UUID.randomUUID());
        negativeAmount.setAmount(-1.0);

        return Stream.of(
                Arguments.of((Object) null,           HttpStatus.OK),
                Arguments.of(List.of(),               HttpStatus.BAD_REQUEST),
                Arguments.of(List.of(nullProductId),  HttpStatus.BAD_REQUEST),
                Arguments.of(List.of(nullAmount),     HttpStatus.BAD_REQUEST),
                Arguments.of(List.of(zeroAmount),     HttpStatus.BAD_REQUEST),
                Arguments.of(List.of(negativeAmount), HttpStatus.BAD_REQUEST)
        );
    }
}
