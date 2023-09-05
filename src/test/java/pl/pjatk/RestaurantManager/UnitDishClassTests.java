package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import pl.pjatk.RestaurantManager.model.Category;
import pl.pjatk.RestaurantManager.model.Dish;
import pl.pjatk.RestaurantManager.model.DishIngredient;
import pl.pjatk.RestaurantManager.model.Ingredient;
import pl.pjatk.RestaurantManager.repository.DishRepository;
import pl.pjatk.RestaurantManager.request.DishRequest;
import pl.pjatk.RestaurantManager.service.CategoryService;
import pl.pjatk.RestaurantManager.service.DishService;
import pl.pjatk.RestaurantManager.service.IngredientService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnitDishClassTests {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private IngredientService ingredientService;

    @InjectMocks
    private DishService dishService;

    @Test
    @DisplayName("Test findAll method")
    void findAll() {
        // given
        List<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish());
        when(dishRepository.findAll()).thenReturn(dishes);

        // when
        List<Dish> result = dishService.findAll();

        // then
        assertEquals(dishes, result);
        verify(dishRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findAllById method")
    void findAllById() {
        // given
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        List<Dish> dishes = new ArrayList<>();
        dishes.add(new Dish());
        when(dishRepository.findAllById(ids)).thenReturn(dishes);

        // when
        List<Dish> result = dishService.findAllById(ids);

        // then
        assertEquals(dishes, result);
        verify(dishRepository, times(1)).findAllById(ids);
    }

    @Test
    @DisplayName("Test findById method")
    void findById() {
        // given
        int dishId = 1;
        Dish dish = new Dish();
        dish.setId(dishId);
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));

        // when
        Optional<Dish> result = dishService.findById(dishId);

        // then
        assertTrue(result.isPresent());
        assertEquals(dish, result.get());
        verify(dishRepository, times(1)).findById(dishId);
    }

    @Test
    @DisplayName("Test add method")
    void addDish() {
        // given
        DishRequest request = new DishRequest();
        request.setName("Test dish");
        request.setDescription("Test description");
        request.setCategoryId(1);
        request.setPrice(BigDecimal.TEN);
        List<Integer> ingredientIds = new ArrayList<>();
        ingredientIds.add(1);
        request.setIngredientIds(ingredientIds);
        byte[] imageBytes = new byte[]{1, 2, 3};
        request.setImage(new MockMultipartFile("image.jpg", "image.jpg", "image/jpeg", imageBytes));

        Category category = new Category();
        category.setId(1);

        Ingredient ingredient = new Ingredient();
        ingredient.setId(1);

        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient);


        when(dishRepository.save(any(Dish.class))).thenAnswer(invocation -> {
            Dish dish = invocation.getArgument(0);
            dish.setId(1); // Przypisanie ID, aby symulować zapis do bazy danych
            return dish;
        });

        // when
        Dish result = dishService.addDish(request);

        // then
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getId(), "Result should have a valid ID");

        verify(dishRepository, times(1)).save(any(Dish.class));
    }




    @Test
    @DisplayName("Test update method")
    void updateDish() {
        // given
        int dishId = 1;
        DishRequest request = new DishRequest();
        request.setName("Updated test dish");
        request.setDescription("Updated test description");
        request.setCategoryId(2);
        request.setPrice(BigDecimal.valueOf(20));
        List<Integer> ingredientIds = new ArrayList<>();
        ingredientIds.add(2);
        request.setIngredientIds(ingredientIds);

        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setName("Test dish");
        dish.setDescription("Test description");
        dish.setPrice(BigDecimal.TEN);
        Category category = new Category();
        category.setId(2);
        dish.setCategory(category);
        List<DishIngredient> dishIngredients = new ArrayList<>();
        dish.setDishIngredients(dishIngredients);

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));
        when(categoryService.findById(2)).thenReturn(Optional.of(category));
        when(dishRepository.save(any())).thenReturn(dish);

        // when
        Dish result = dishService.updateDish(dishId, request);

        // then
        assertNotNull(result);
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getPrice(), result.getPrice());
        assertEquals(request.getCategoryId(), result.getCategory().getId());
        assertTrue(result.getDishIngredients().isEmpty());
        verify(dishRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Test delete method if dish exists")
    void deleteDish_shouldReturnTrue_whenDishExists() {
        // given
        Dish dish = new Dish();
        dish.setId(1);
        when(dishRepository.findById(dish.getId())).thenReturn(Optional.of(dish));

        // when
        boolean result = dishService.deleteDish(dish.getId());

        // then
        verify(dishRepository, times(1)).deleteById(dish.getId());
        assertTrue(result);
    }

    @Test
    @DisplayName("Test delete method if dish doesn't exist")
    void deleteDish_shouldReturnFalse_whenDishDoesNotExist() {
        // given
        int dishId = 1;
        when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        // when
        boolean result = dishService.deleteDish(dishId);

        // then
        assertFalse(result);
    }
}
