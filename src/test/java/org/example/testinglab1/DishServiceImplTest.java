package org.example.testinglab1;

import org.example.testinglab1.dto.request.GetDishNutritionRequest;
import org.example.testinglab1.dto.response.NutritionDto;
import org.example.testinglab1.entity.Product;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.exception.NotFoundException;
import org.example.testinglab1.mapper.DishMapper;
import org.example.testinglab1.repository.DishRepository;
import org.example.testinglab1.repository.ProductRepository;
import org.example.testinglab1.service.impl.DishServiceImpl;
import org.example.testinglab1.specification.DishSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DishServiceImplTest {
    @Mock
    private DishRepository dishRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DishMapper dishMapper;

    @Mock
    private DishSpecification dishSpecification;

    @InjectMocks
    private DishServiceImpl dishService;

    private Product buildProduct(double calories, double proteins,
                                 double fats, double carbs, Flag... flags) {
        Product p = new Product();
        p.setCalories(calories);
        p.setProteins(proteins);
        p.setFats(fats);
        p.setCarbohydrates(carbs);
        p.setFlags(new HashSet<>(Arrays.asList(flags)));
        return p;
    }

    private GetDishNutritionRequest buildRequest(List<UUID> ids, List<Integer> amounts) {
        GetDishNutritionRequest req = new GetDishNutritionRequest();
        req.setIngredientsIds(ids);
        req.setIngredientsAmounts(amounts);
        return req;
    }

    // Один продукт, типичное количество 100г
    @Test
    @DisplayName("Тест 1")
    void test1() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(200.0, 10.0, 5.0, 30.0, Flag.VEGAN);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        NutritionDto result = dishService.calcNutrition(buildRequest(
                List.of(id), List.of(100)
        ));

        assertThat(result.getCalories()).isEqualTo(200.0);
        assertThat(result.getProteins()).isEqualTo(10.0);
        assertThat(result.getFats()).isEqualTo(5.0);
        assertThat(result.getCarbohydrates()).isEqualTo(30.0);
        assertThat(result.getAllowedFlags()).containsExactly(Flag.VEGAN);
    }

    // Граничное значение amount = 1г
    @Test
    @DisplayName("Тест 2")
    void test2() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(200.0, 10.0, 5.0, 30.0);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        NutritionDto result = dishService.calcNutrition(buildRequest(
                List.of(id), List.of(1)
        ));

        assertThat(result.getCalories()).isEqualTo(2.0);
        assertThat(result.getProteins()).isEqualTo(0.1);
        assertThat(result.getFats()).isEqualTo(0.05);
        assertThat(result.getCarbohydrates()).isEqualTo(0.30);
    }

    // Граничное значение amount = 0г
    @Test
    @DisplayName("Тест 3")
    void test3() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(200.0, 10.0, 5.0, 30.0);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        NutritionDto result = dishService.calcNutrition(buildRequest(
                List.of(id), List.of(0)
        ));

        assertThat(result.getCalories()).isEqualTo(0.0);
        assertThat(result.getProteins()).isEqualTo(0.0);
        assertThat(result.getFats()).isEqualTo(0.0);
        assertThat(result.getCarbohydrates()).isEqualTo(0.0);
    }

    // Большое количество 1000000г
    @Test
    @DisplayName("Тест 4")
    void test4() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(200.0, 10.0, 5.0, 30.0);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        NutritionDto result = dishService.calcNutrition(buildRequest(
                List.of(id), List.of(1000000)
        ));

        assertThat(result.getCalories()).isEqualTo(2000000.0);
        assertThat(result.getProteins()).isEqualTo(100000.0);
        assertThat(result.getFats()).isEqualTo(50000.0);
        assertThat(result.getCarbohydrates()).isEqualTo(300000.0);
    }

    // Несколько продуктов
    @Test
    @DisplayName("Тест 5")
    void test5() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Product p1 = buildProduct(100.0, 5.0, 2.0, 10.0);
        Product p2 = buildProduct(200.0, 8.0, 4.0, 20.0);

        when(productRepository.findById(id1)).thenReturn(Optional.of(p1));
        when(productRepository.findById(id2)).thenReturn(Optional.of(p2));

        NutritionDto result = dishService.calcNutrition(buildRequest(
                List.of(id1, id2), List.of(100, 100)
        ));

        assertThat(result.getCalories()).isEqualTo(300.0);
        assertThat(result.getProteins()).isEqualTo(13.0);
        assertThat(result.getFats()).isEqualTo(6.0);
        assertThat(result.getCarbohydrates()).isEqualTo(30.0);
    }

    // Продукт с нулевыми значениями
    @Test
    @DisplayName("Тест 6")
    void test6() {
        UUID id = UUID.randomUUID();
        Product product = buildProduct(0.0, 0.0, 0.0, 0.0);
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        NutritionDto result = dishService.calcNutrition(buildRequest(
                List.of(id), List.of(100)
        ));

        assertThat(result.getCalories()).isEqualTo(0.0);
        assertThat(result.getProteins()).isEqualTo(0.0);
        assertThat(result.getFats()).isEqualTo(0.0);
        assertThat(result.getCarbohydrates()).isEqualTo(0.0);
    }

    // Проверка одинаковых флагов
    @Test
    @DisplayName("Тест 7")
    void test7() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Product p1 = buildProduct(100.0, 5.0, 2.0, 10.0, Flag.VEGAN);
        Product p2 = buildProduct(200.0, 8.0, 4.0, 20.0, Flag.VEGAN, Flag.GLUTEN_FREE);

        when(productRepository.findById(id1)).thenReturn(Optional.of(p1));
        when(productRepository.findById(id2)).thenReturn(Optional.of(p2));

        NutritionDto result = dishService.calcNutrition(buildRequest(
                List.of(id1, id2), List.of(50, 50)
        ));

        assertThat(result.getAllowedFlags())
                .containsExactly(Flag.VEGAN);
    }

    // Проверка разных флагов
    @Test
    @DisplayName("Тест 8")
    void test8() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Product p1 = buildProduct(100.0, 5.0, 2.0, 10.0, Flag.VEGAN);
        Product p2 = buildProduct(200.0, 8.0, 4.0, 20.0, Flag.GLUTEN_FREE);

        when(productRepository.findById(id1)).thenReturn(Optional.of(p1));
        when(productRepository.findById(id2)).thenReturn(Optional.of(p2));

        NutritionDto result = dishService.calcNutrition(buildRequest(
                List.of(id1, id2), List.of(50, 50)
        ));

        assertThat(result.getAllowedFlags()).isEmpty();
    }

    // Несуществующий ID продукта
    @Test
    @DisplayName("Тест 9")
    void test9() {
        UUID unknownId = UUID.randomUUID();
        when(productRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dishService.calcNutrition(buildRequest(
                List.of(unknownId), List.of(100)
        )))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(unknownId.toString());
    }
}
