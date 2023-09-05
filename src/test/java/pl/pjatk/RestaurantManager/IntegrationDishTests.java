package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.RestaurantManager.model.Category;
import pl.pjatk.RestaurantManager.model.Dish;
import pl.pjatk.RestaurantManager.model.Ingredient;
import pl.pjatk.RestaurantManager.repository.CategoryRepository;
import pl.pjatk.RestaurantManager.repository.DishIngredientRepository;
import pl.pjatk.RestaurantManager.repository.DishRepository;
import pl.pjatk.RestaurantManager.repository.IngredientRepository;
import pl.pjatk.RestaurantManager.request.DishRequest;
import pl.pjatk.RestaurantManager.service.CategoryService;
import pl.pjatk.RestaurantManager.service.DishService;
import pl.pjatk.RestaurantManager.service.IngredientService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class IntegrationDishTests {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private DishIngredientRepository dishIngredientRepository;

    @Test
    public void testFindAll() {
        // given
        Category category = new Category();
        category.setName("Main Course");
        categoryRepository.save(category);

        Dish dish = new Dish();
        dish.setName("Pasta");
        dish.setDescription("Delicious pasta");
        dish.setCategory(category);
        dish.setPrice(BigDecimal.valueOf(10.99));
        dishRepository.save(dish);

        // when
        List<Dish> dishes = dishService.findAll();

        // then
        Assertions.assertFalse(dishes.isEmpty());
    }

    @Test
    public void testFindAllById() {
        // given
        Category category = new Category();
        category.setName("Main Course");
        categoryRepository.save(category);

        Dish dish1 = new Dish();
        dish1.setName("Pasta");
        dish1.setDescription("Delicious pasta");
        dish1.setCategory(category);
        dish1.setPrice(BigDecimal.valueOf(10.99));
        dishRepository.save(dish1);

        Dish dish2 = new Dish();
        dish2.setName("Pizza");
        dish2.setDescription("Yummy pizza");
        dish2.setCategory(category);
        dish2.setPrice(BigDecimal.valueOf(8.99));
        dishRepository.save(dish2);

        // when
        List<Dish> dishes = dishService.findAllById(List.of(dish1.getId(), dish2.getId()));

        // then
        Assertions.assertEquals(2, dishes.size());
    }

    @Test
    public void testFindById() {
        // given
        Category category = new Category();
        category.setName("Main Course");
        categoryRepository.save(category);

        Dish dish = new Dish();
        dish.setName("Pasta");
        dish.setDescription("Delicious pasta");
        dish.setCategory(category);
        dish.setPrice(BigDecimal.valueOf(10.99));
        dishRepository.save(dish);

        // when
        Optional<Dish> foundDish = dishService.findById(dish.getId());

        // then
        Assertions.assertTrue(foundDish.isPresent());
        Assertions.assertEquals("Pasta", foundDish.get().getName());
        Assertions.assertEquals(category.getId(), foundDish.get().getCategory().getId());
    }

    @Test
    public void testAddDish() {
        // given
        Category category = new Category();
        category.setName("Main Course");
        categoryRepository.save(category);

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Flour");
        ingredientRepository.save(ingredient);

        DishRequest dishRequest = new DishRequest();
        dishRequest.setName("Pasta");
        dishRequest.setDescription("Delicious pasta");
        dishRequest.setCategoryId(category.getId());
        dishRequest.setPrice(BigDecimal.valueOf(10.99));
        dishRequest.setIngredientIds(Collections.singletonList(ingredient.getId()));
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[10]);
        dishRequest.setImage(image);
        // when
        Dish savedDish = dishService.addDish(dishRequest);

        // then
        Assertions.assertNotNull(savedDish.getId());
        Assertions.assertEquals("Pasta", savedDish.getName());
        Assertions.assertEquals(category.getId(), savedDish.getCategory().getId());
        Assertions.assertFalse(savedDish.getDishIngredients().isEmpty());
    }

    @Test
    public void testUpdateDish() {
        // given
        Category category = new Category();
        category.setName("Main Course");
        categoryRepository.save(category);

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Flour");
        ingredientRepository.save(ingredient);

        Dish dish = new Dish();
        dish.setName("Pasta");
        dish.setDescription("Delicious pasta");
        dish.setCategory(category);
        dish.setPrice(BigDecimal.valueOf(10.99));
        dish.setDishIngredients(new ArrayList<>()); // Initialize the dishIngredients list
        dishRepository.save(dish);

        DishRequest updatedDishRequest = new DishRequest();
        updatedDishRequest.setName("Pizza");
        updatedDishRequest.setDescription("Yummy pizza");
        updatedDishRequest.setCategoryId(category.getId());
        updatedDishRequest.setPrice(BigDecimal.valueOf(8.99));
        updatedDishRequest.setIngredientIds(Collections.singletonList(ingredient.getId()));

        // when
        Optional<Dish> updatedDish = Optional.ofNullable(dishService.updateDish(dish.getId(), updatedDishRequest));

        // then
        Assertions.assertTrue(updatedDish.isPresent());
        Assertions.assertEquals("Pizza", updatedDish.get().getName());
        Assertions.assertEquals(category.getId(), updatedDish.get().getCategory().getId());
        Assertions.assertFalse(updatedDish.get().getDishIngredients().isEmpty());
    }


    @Test
    public void testDeleteDish() {
        // given
        Category category = new Category();
        category.setName("Main Course");
        categoryRepository.save(category);

        Dish dish = new Dish();
        dish.setName("Pasta");
        dish.setDescription("Delicious pasta");
        dish.setCategory(category);
        dish.setPrice(BigDecimal.valueOf(10.99));
        dish = dishRepository.save(dish);

        // when
        boolean result = dishService.deleteDish(dish.getId());

        // then
        Assertions.assertTrue(result);
        Assertions.assertFalse(dishRepository.existsById(dish.getId()));
    }
}
