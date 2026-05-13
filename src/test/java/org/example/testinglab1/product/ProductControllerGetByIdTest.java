package org.example.testinglab1.product;

import org.example.testinglab1.dto.response.ProductFullDto;
import org.example.testinglab1.entity.Product;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.enums.ProductCategory;
import org.example.testinglab1.enums.Readiness;
import org.example.testinglab1.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class ProductControllerGetByIdTest {
    @Autowired
    private RestTestClient client;

    @Autowired
    private ProductRepository productRepository;

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

    @ParameterizedTest(name = "Невалидные id, должен вернуть 400")
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
    void test1 (String id){
        var response = client.get()
                .uri("/products/" + id)
                .exchange()
                .expectStatus().isBadRequest()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Валидный id, продукт существует, должен вернуть тело + 200")
    void test2 (){
        Product saved = createProduct();

        var response = client.get()
                .uri("/products/" + saved.getId())
                .exchange()
                .expectStatus().isOk()
                .returnResult(ProductFullDto.class);

        ProductFullDto body = response.getResponseBody();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(saved.getId());
        assertThat(body.getName()).isEqualTo("Свекла");
        assertThat(body.getCalories()).isEqualTo(88.0);
        assertThat(body.getProteins()).isEqualTo(1.5);
        assertThat(body.getFats()).isEqualTo(0.1);
        assertThat(body.getCarbohydrates()).isEqualTo(8.8);
        assertThat(body.getComposition()).isEqualTo("Корнеплод");
        assertThat(body.getCategory()).isEqualTo(ProductCategory.VEGETABLES);
        assertThat(body.getReadiness()).isEqualTo(Readiness.REQUIRES_COOKING);
        assertThat(body.getFlags()).isEqualTo(Set.of(Flag.VEGAN));
        assertThat(body.getPhotos()).isEqualTo(List.of("svekla.png"));
        assertThat(body.getCreatedAt()).isNotNull();
        assertThat(body.getUpdatedAt()).isNull();

        productRepository.deleteById(saved.getId());
    }

    @Test
    @DisplayName("Валидный id, продукта с таким id не существует, должен вернуть 404")
    void test3 (){
        var response = client.get()
                .uri("/products/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound()
                .returnResult(String.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
