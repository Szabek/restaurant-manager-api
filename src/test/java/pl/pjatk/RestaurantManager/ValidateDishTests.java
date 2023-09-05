package pl.pjatk.RestaurantManager;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import pl.pjatk.RestaurantManager.model.Dish;
import pl.pjatk.RestaurantManager.repository.DishIngredientRepository;
import pl.pjatk.RestaurantManager.repository.DishRepository;
import pl.pjatk.RestaurantManager.request.DishRequest;
import pl.pjatk.RestaurantManager.service.CategoryService;
import pl.pjatk.RestaurantManager.service.DishService;
import pl.pjatk.RestaurantManager.service.IngredientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ValidateDishTests {
    @Mock
    private DishRepository dishRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private DishIngredientRepository dishIngredientRepository;

    @InjectMocks
    private DishService dishService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test findAll method")
    public void testFindAll() {
        // Given
        List<Dish> dishes = new ArrayList<>();
        when(dishRepository.findAll()).thenReturn(dishes);

        // When
        List<Dish> result = dishService.findAll();

        // Then
        assertEquals(dishes, result);
    }

    @Test
    @DisplayName("Test findAllById method")
    public void testFindAllById() {
        // Given
        List<Integer> ids = List.of(1, 2, 3);
        List<Dish> dishes = new ArrayList<>();
        when(dishRepository.findAllById(ids)).thenReturn(dishes);

        // When
        List<Dish> result = dishService.findAllById(ids);

        // Then
        assertEquals(dishes, result);
    }

    @Test
    @DisplayName("Test findById method with existing id")
    public void testFindById() {
        // Given
        Integer id = 1;
        Dish dish = new Dish();
        when(dishRepository.findById(id)).thenReturn(Optional.of(dish));

        // When
        Optional<Dish> result = dishService.findById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(dish, result.get());
    }

    @Test
    @DisplayName("Test findById method with non-existent id")
    public void testFindByIdNotFound() {
        // Given
        Integer id = 1;
        when(dishRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Dish> result = dishService.findById(id);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test addDish method")
    public void testAddDish() {
        // Given
        DishRequest request = new DishRequest();
        request.setIngredientIds(new ArrayList<>()); // Initialize the ingredientIds list
        Dish dish = new Dish();
        byte[] imageBytes = new byte[]{1, 2, 3};
        request.setImage(new MockMultipartFile("image.jpg", "image.jpg", "image/jpeg", imageBytes));
        when(categoryService.findById(anyInt())).thenReturn(Optional.empty());
        when(ingredientService.findById(anyInt())).thenReturn(Optional.empty());
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);

        // When
        Dish result = dishService.addDish(request);

        // Then
        assertEquals(dish, result);
    }


    @Test
    @DisplayName("Test updateDish method with existing id")
    void testUpdateDish() {
        // Given
        Integer dishId = 1;
        DishRequest request = new DishRequest();
        request.setIngredientIds(new ArrayList<>()); // Initialize the ingredientIds list

        Dish dish = new Dish();
        dish.setId(dishId); // Set the dish id
        dish.setDishIngredients(new ArrayList<>()); // Initialize the dishIngredients list

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);

        // When
        Dish result = dishService.updateDish(dishId, request);

        // Then
        assertEquals(dish, result);
        verify(dishRepository, times(1)).save(any(Dish.class));
    }




    @Test
    @DisplayName("Test updateDish method with non-existent id")
    public void testUpdateDishNotFound() {
        // Given
        Integer dishId = 1;
        DishRequest request = new DishRequest();
        when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> dishService.updateDish(dishId, request));
    }

    @Test
    @DisplayName("Test deleteDish method with existing id")
    public void testDeleteDish() {
        // Given
        Integer id = 1;
        when(dishRepository.findById(id)).thenReturn(Optional.of(new Dish()));

        // When
        boolean result = dishService.deleteDish(id);

        // Then
        assertTrue(result);
        verify(dishRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Test deleteDish method with non-existent id")
    public void testDeleteDishNotFound() {
        // Given
        Integer id = 1;
        when(dishRepository.findById(id)).thenReturn(Optional.empty());

        // When
        boolean result = dishService.deleteDish(id);

        // Then
        assertFalse(result);
        verify(dishRepository, never()).deleteById(anyInt());
    }
}
