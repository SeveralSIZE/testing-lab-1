package org.example.testinglab1.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.testinglab1.dto.filter.DishFilter;
import org.example.testinglab1.dto.request.CreateDishRequest;
import org.example.testinglab1.dto.request.IngredientRequest;
import org.example.testinglab1.dto.request.UpdateDishRequest;
import org.example.testinglab1.dto.response.DishFullDto;
import org.example.testinglab1.dto.response.DishPageDto;
import org.example.testinglab1.entity.Dish;
import org.example.testinglab1.entity.DishProduct;
import org.example.testinglab1.entity.Product;
import org.example.testinglab1.enums.DishCategory;
import org.example.testinglab1.enums.Flag;
import org.example.testinglab1.exception.NotFoundException;
import org.example.testinglab1.exception.InvalidMacroRatioException;
import org.example.testinglab1.mapper.DishMapper;
import org.example.testinglab1.repository.DishRepository;
import org.example.testinglab1.repository.ProductRepository;
import org.example.testinglab1.service.DishService;
import org.example.testinglab1.specification.DishSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final ProductRepository productRepository;
    private final DishMapper dishMapper;
    private final DishSpecification dishSpecification;

    @Override
    public UUID create(CreateDishRequest request) {
        String name = request.getName();
        DishCategory category = request.getCategory();

        MacroCategory macroCategory = parseCategoryMacro(name);
        name = macroCategory.macro();

        if (category == null) {
            category = macroCategory.category();
        }

        List<DishProduct> ingredients = resolveIngredients(null, request.getIngredients());

        // 2.2 Автоматический расчёт КБЖУ
        NutritionValues calculated = calculateNutrition(ingredients, request.getPortionSize());

        Double proteins = request.getProteins() != null ? request.getProteins() : calculated.proteins();
        Double fats = request.getFats() != null ? request.getFats() : calculated.fats();
        Double carbohydrates = request.getCarbohydrates() != null ? request.getCarbohydrates() : calculated.carbohydrates();
        Double calories = request.getCalories() != null ? request.getCalories() : calculated.calories();

        if (proteins + fats + carbohydrates > 100) {
            throw new InvalidMacroRatioException("Сумма бжу больше 100 грамм");
        }

        // 2.4 Валидация и фильтрация флагов
        Set<Flag> flags = validateAndFilterFlags(request.getFlags(), ingredients);

        Dish dish = dishMapper.toEntity(request, name, category, calories, proteins, fats, carbohydrates, flags);
        ingredients.forEach(i -> i.setDish(dish));
        dish.setIngredients(ingredients);

        dishRepository.save(dish);
        return dish.getId();
    }

    @Override
    public DishPageDto getAll(Pageable pageable, DishFilter filter) {
        Page<Dish> page = dishRepository.findAll(dishSpecification.getSpecification(filter), pageable);
        return dishMapper.toPageDto(page);
    }

    @Override
    public DishFullDto getById(UUID id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Блюдо с id: " + id + " не найдено"));
        return dishMapper.toFullDto(dish);
    }

    @Override
    public void deleteById(UUID id) {
        dishRepository.deleteById(id);
    }

    @Override
    public void updateById(UUID id, UpdateDishRequest request) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Блюдо с id: " + id + " не найдено"));

        // 2.3 Макросы при редактировании
        String name = request.getName() != null ? request.getName() : dish.getName();
        DishCategory category = request.getCategory() != null ? request.getCategory() : dish.getCategory();

        if (request.getName() != null) {
            MacroCategory macroResult = parseCategoryMacro(name);
            name = macroResult.macro();
            if (request.getCategory() == null) {
                category = macroResult.category() != null ? macroResult.category() : dish.getCategory();
            }
        }

        // 2.7 Пересчёт ингредиентов если переданы
        List<DishProduct> ingredients;
        if (request.getIngredients() != null) {
            ingredients = resolveIngredients(dish, request.getIngredients());
        } else {
            ingredients = dish.getIngredients();
        }

        Double portionSize = request.getPortionSize() != null ? request.getPortionSize() : dish.getPortionSize();

        // 2.2 Автоматический пересчёт КБЖУ (черновые значения)
        NutritionValues calculated = calculateNutrition(ingredients, portionSize);

        Double proteins = request.getProteins() != null ? request.getProteins() : calculated.proteins();
        Double fats = request.getFats() != null ? request.getFats() : calculated.fats();
        Double carbohydrates = request.getCarbohydrates() != null ? request.getCarbohydrates() : calculated.carbohydrates();
        Double calories = request.getCalories() != null ? request.getCalories() : calculated.calories();

        if (proteins + fats + carbohydrates > 100) {
            throw new InvalidMacroRatioException("Сумма БЖУ на 100 грамм не может превышать 100");
        }

        // 2.4 Пересчёт флагов при изменении состава
        Set<Flag> requestedFlags = request.getFlags() != null ? request.getFlags() : dish.getFlags();
        Set<Flag> flags = validateAndFilterFlags(requestedFlags, ingredients);

        dish.setName(name);
        dish.setCategory(category);
        dish.setCalories(calories);
        dish.setProteins(proteins);
        dish.setFats(fats);
        dish.setCarbohydrates(carbohydrates);
        dish.setFlags(flags);
        dish.setPortionSize(portionSize);

        if (request.getPhotos() != null) {
            dish.setPhotos(request.getPhotos());
        }
        if (request.getIngredients() != null) {
            dish.getIngredients().clear();
            ingredients.forEach(i -> i.setDish(dish));
            dish.getIngredients().addAll(ingredients);
        }

        dishRepository.save(dish);
    }

    private List<DishProduct> resolveIngredients(Dish dish, List<IngredientRequest> ingredientRequests) {
        if (ingredientRequests == null || ingredientRequests.isEmpty()) {
            return new ArrayList<>();
        }

        return ingredientRequests.stream().map(req -> {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new NotFoundException("Продукт с id: " + req.getProductId() + " не найден"));

            DishProduct dp = new DishProduct();
            dp.setId(new DishProduct.DishProductId(
                    dish != null ? dish.getId() : null,
                    product.getId()
            ));
            dp.setProduct(product);
            dp.setAmount(req.getAmount());
            return dp;
        }).toList();
    }

    private NutritionValues calculateNutrition(List<DishProduct> ingredients, Double portionSize) {
        if (ingredients.isEmpty() || portionSize == null || portionSize == 0) {
            return new NutritionValues(0.0, 0.0, 0.0, 0.0);
        }

        double totalProteins = 0, totalFats = 0, totalCarbs = 0, totalCalories = 0;

        for (DishProduct dp : ingredients) {
            Product p = dp.getProduct();
            double factor = dp.getAmount() / 100.0;
            totalProteins += p.getProteins() * factor;
            totalFats += p.getFats() * factor;
            totalCarbs += p.getCarbohydrates() * factor;
            totalCalories += p.getCalories() * factor;
        }

        double totalWeight = ingredients.stream().mapToDouble(DishProduct::getAmount).sum();
        double normFactor = totalWeight > 0 ? 100.0 / totalWeight : 1.0;

        return new NutritionValues(
                totalCalories * normFactor,
                totalProteins * normFactor,
                totalFats * normFactor,
                totalCarbs * normFactor
        );
    }

    private MacroCategory parseCategoryMacro(String name) {
        if (name == null) return new MacroCategory(null, null);

        Map<String, DishCategory> macros = Map.of(
                "!десерт", DishCategory.DESSERT,
                "!первое", DishCategory.FIRST,
                "!второе", DishCategory.SECOND,
                "!напиток", DishCategory.DRINK,
                "!салат", DishCategory.SALAD,
                "!суп", DishCategory.SOUP,
                "!перекус", DishCategory.SNACK
        );

        DishCategory foundCategory = null;
        for (Map.Entry<String, DishCategory> entry : macros.entrySet()) {
            if (name.contains(entry.getKey())) {
                if (foundCategory == null) {
                    foundCategory = entry.getValue();
                }
                name = name.replace(entry.getKey(), "").trim();
            }
        }

        return new MacroCategory(name, foundCategory);
    }

    private Set<Flag> validateAndFilterFlags(Set<Flag> requestedFlags, List<DishProduct> ingredients) {
        if (requestedFlags == null || requestedFlags.isEmpty()) {
            return new HashSet<>();
        }

        Set<Flag> result = new HashSet<>();
        for (Flag flag : requestedFlags) {
            boolean allHaveFlag = ingredients.stream()
                    .allMatch(dp -> dp.getProduct().getFlags().contains(flag));
            if (allHaveFlag) {
                result.add(flag);
            }
        }
        return result;
    }

    private record NutritionValues(Double calories, Double proteins, Double fats, Double carbohydrates) {}
    private record MacroCategory(String macro, DishCategory category) {}
}
