package org.example.testinglab1.product;

import org.example.testinglab1.dto.request.CreateProductRequest;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.enums.ProductCategory;
import org.example.testinglab1.enums.Readiness;
import org.example.testinglab1.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
public class ProductControllerCreateTest {

    @Autowired
    private RestTestClient client;

    @Autowired
    private ProductRepository productRepository;

    private CreateProductRequest validRequest() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Свекла");
        request.setCalories(88.0);
        request.setProteins(1.5);
        request.setFats(0.1);
        request.setCarbohydrates(8.8);
        request.setComposition("Корнеплод");
        request.setCategory(ProductCategory.VEGETABLES);
        request.setReadiness(Readiness.REQUIRES_COOKING);
        request.setFlags(Set.of(Flag.VEGAN));
        request.setPhotos(List.of("svekla.png"));
        return request;
    }

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Валидный запрос со всеми полями, должен вернуть UUID + 200")
    void test1() {
        var response = client.post()
                .uri("/products")
                .body(validRequest())
                .exchange()
                .expectStatus().isOk()
                .returnResult(UUID.class);

        UUID id = response.getResponseBody();
        assertThat(id).isNotNull();
        assertThat(productRepository.findById(id)).isPresent();
    }

    @Test
    @DisplayName("Валидный запрос без необязательных полей, должен вернуть UUID + 200")
    void test2() {
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Морковь");
        request.setCalories(41.0);
        request.setProteins(0.9);
        request.setFats(0.2);
        request.setCarbohydrates(6.9);
        request.setCategory(ProductCategory.VEGETABLES);
        request.setReadiness(Readiness.REQUIRES_COOKING);

        var response = client.post()
                .uri("/products")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .returnResult(UUID.class);

        UUID id = response.getResponseBody();
        assertThat(id).isNotNull();
        assertThat(productRepository.findById(id)).isPresent();
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения name")
    @MethodSource("nameProvider")
    void test3(String name, HttpStatus expectedStatus) {
        CreateProductRequest request = validRequest();
        request.setName(name);

        var response = client.post()
                .uri("/products")
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения calories")
    @MethodSource("caloriesProvider")
    void test4(Double calories, HttpStatus expectedStatus) {
        CreateProductRequest request = validRequest();
        request.setCalories(calories);

        var response = client.post()
                .uri("/products")
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения proteins, fats, carbohydrates")
    @MethodSource("bjuProvider")
    void test5(Double proteins, Double fats, Double carbohydrates, HttpStatus expectedStatus) {
        CreateProductRequest request = validRequest();
        request.setProteins(proteins);
        request.setFats(fats);
        request.setCarbohydrates(carbohydrates);

        var response = client.post()
                .uri("/products")
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения photos")
    @MethodSource("photosProvider")
    void test6(List<String> photos, HttpStatus expectedStatus) {
        CreateProductRequest request = validRequest();
        request.setPhotos(photos);

        var response = client.post()
                .uri("/products")
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все граничные, валидные и невалидные значения composition")
    @MethodSource("compositionProvider")
    void test7(String composition, HttpStatus expectedStatus) {
        CreateProductRequest request = validRequest();
        request.setComposition(composition);

        var response = client.post()
                .uri("/products")
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все валидные и невалидные значения category")
    @MethodSource("categoryProvider")
    void test8(ProductCategory category, HttpStatus expectedStatus) {
        CreateProductRequest request = validRequest();
        request.setCategory(category);

        var response = client.post()
                .uri("/products")
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все валидные и невалидные значения readiness")
    @MethodSource("readinessProvider")
    void test9(Readiness readiness, HttpStatus expectedStatus) {
        CreateProductRequest request = validRequest();
        request.setReadiness(readiness);

        var response = client.post()
                .uri("/products")
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @DisplayName("Все валидные значения flags")
    @MethodSource("flagsProvider")
    void test10(Set<Flag> flags, HttpStatus expectedStatus) {
        CreateProductRequest request = validRequest();
        request.setFlags(flags);

        var response = client.post()
                .uri("/products")
                .body(request)
                .exchange()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(expectedStatus);
    }

    @Test
    @DisplayName("Полностью пустой запрос, должен вернуть 400")
    void test11() {
        CreateProductRequest request = new CreateProductRequest();

        client.post()
                .uri("/products")
                .body(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    static Stream<Arguments> nameProvider() {
        return Stream.of(
                Arguments.of("Свекла",        HttpStatus.OK),
                Arguments.of("Св",            HttpStatus.OK),
                Arguments.of("Све",           HttpStatus.OK),
                Arguments.of("С".repeat(254), HttpStatus.OK),
                Arguments.of("С".repeat(255), HttpStatus.OK),
                Arguments.of("С",             HttpStatus.BAD_REQUEST),
                Arguments.of("С".repeat(256), HttpStatus.BAD_REQUEST),
                Arguments.of("",              HttpStatus.BAD_REQUEST),
                Arguments.of(" ",             HttpStatus.BAD_REQUEST),
                Arguments.of("   ",           HttpStatus.BAD_REQUEST),
                Arguments.of((Object) null,   HttpStatus.BAD_REQUEST)
        );
    }

    static Stream<Arguments> caloriesProvider() {
        return Stream.of(
                Arguments.of(75.0,       HttpStatus.OK),
                Arguments.of(0.0,        HttpStatus.OK),
                Arguments.of(0.1,        HttpStatus.OK),
                Arguments.of(1000000.0,  HttpStatus.OK),
                Arguments.of(-0.1,       HttpStatus.BAD_REQUEST),
                Arguments.of(-1.0,       HttpStatus.BAD_REQUEST),
                Arguments.of(null,       HttpStatus.BAD_REQUEST)
        );
    }

    static Stream<Arguments> bjuProvider() {
        return Stream.of(
                Arguments.of(0.0,   0.0,   0.0,   HttpStatus.OK),
                Arguments.of(0.1,   0.1,   0.1,   HttpStatus.OK),
                Arguments.of(99.9,  0.0,   0.0,   HttpStatus.OK),
                Arguments.of(100.0, 0.0,   0.0,   HttpStatus.OK),
                Arguments.of(0.0,   100.0, 0.0,   HttpStatus.OK),
                Arguments.of(0.0,   0.0,   100.0, HttpStatus.OK),
                Arguments.of(33.3,  33.3,  33.4,  HttpStatus.OK),
                Arguments.of(1.5,   0.1,   8.8,   HttpStatus.OK),
                Arguments.of(-0.1,  0.0,   0.0,   HttpStatus.BAD_REQUEST),
                Arguments.of(100.1, 0.0,   0.0,   HttpStatus.BAD_REQUEST),
                Arguments.of(0.0,   -0.1,  0.0,   HttpStatus.BAD_REQUEST),
                Arguments.of(0.0,   100.1, 0.0,   HttpStatus.BAD_REQUEST),
                Arguments.of(0.0,   0.0,   -0.1,  HttpStatus.BAD_REQUEST),
                Arguments.of(0.0,   0.0,   100.1, HttpStatus.BAD_REQUEST),
                Arguments.of(50.0,  50.0,  0.1,   HttpStatus.BAD_REQUEST),
                Arguments.of(null,  0.0,   0.0,   HttpStatus.BAD_REQUEST),
                Arguments.of(0.0,   null,  0.0,   HttpStatus.BAD_REQUEST),
                Arguments.of(0.0,   0.0,   null,  HttpStatus.BAD_REQUEST),
                Arguments.of(null,  null,  null,  HttpStatus.BAD_REQUEST)
        );
    }

    static Stream<Arguments> photosProvider() {
        return Stream.of(
                Arguments.of((Object) null,                                                                HttpStatus.OK),
                Arguments.of(List.of(),                                                                    HttpStatus.OK),
                Arguments.of(List.of("p1.png"),                                                            HttpStatus.OK),
                Arguments.of(List.of("p1.png", "p2.png", "p3.png", "p4.png"),                             HttpStatus.OK),
                Arguments.of(List.of("p1.png", "p2.png", "p3.png", "p4.png", "p5.png"),                   HttpStatus.OK),
                Arguments.of(List.of("p1.png", "p2.png", "p3.png", "p4.png", "p5.png", "p6.png"),         HttpStatus.BAD_REQUEST)
        );
    }

    static Stream<Arguments> compositionProvider() {
        return Stream.of(
                Arguments.of((Object) null,    HttpStatus.OK),
                Arguments.of("",               HttpStatus.OK),
                Arguments.of("Корнеплод",      HttpStatus.OK),
                Arguments.of("А".repeat(1000), HttpStatus.OK)
        );
    }

    static Stream<Arguments> categoryProvider() {
        return Stream.of(
                Arguments.of(ProductCategory.FROZEN,     HttpStatus.OK),
                Arguments.of(ProductCategory.MEAT,       HttpStatus.OK),
                Arguments.of(ProductCategory.VEGETABLES, HttpStatus.OK),
                Arguments.of(ProductCategory.HERBS,      HttpStatus.OK),
                Arguments.of(ProductCategory.SPICES,     HttpStatus.OK),
                Arguments.of(ProductCategory.CEREALS,    HttpStatus.OK),
                Arguments.of(ProductCategory.CANNED,     HttpStatus.OK),
                Arguments.of(ProductCategory.LIQUID,     HttpStatus.OK),
                Arguments.of(ProductCategory.SWEETS,     HttpStatus.OK),
                Arguments.of((Object) null,              HttpStatus.BAD_REQUEST)
        );
    }

    static Stream<Arguments> readinessProvider() {
        return Stream.of(
                Arguments.of(Readiness.READY_TO_EAT,     HttpStatus.OK),
                Arguments.of(Readiness.SEMI_FINISHED,    HttpStatus.OK),
                Arguments.of(Readiness.REQUIRES_COOKING, HttpStatus.OK),
                Arguments.of((Object) null,              HttpStatus.BAD_REQUEST)
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
}