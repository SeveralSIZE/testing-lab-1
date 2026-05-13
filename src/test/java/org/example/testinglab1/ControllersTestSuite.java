package org.example.testinglab1;

import org.example.testinglab1.dish.*;
import org.example.testinglab1.product.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ProductControllerGetByIdTest.class,
        ProductControllerCreateTest.class,
        ProductControllerUpdateTest.class,
        ProductControllerGetAllTest.class,
        ProductControllerDeleteTest.class,
        DishControllerCreateTest.class,
        DishControllerUpdateTest.class,
        DishControllerGetAllTest.class,
        DishControllerGetByIdTest.class,
        DishControllerDeleteTest.class,
        DishControllerCalcNutritionTest.class
})
public class ControllersTestSuite {
}
