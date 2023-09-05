package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pl.pjatk.RestaurantManager.model.Ingredient;
import pl.pjatk.RestaurantManager.model.Unit;
import pl.pjatk.RestaurantManager.repository.IngredientRepository;
import pl.pjatk.RestaurantManager.request.IngredientRequest;
import pl.pjatk.RestaurantManager.service.IngredientService;
import pl.pjatk.RestaurantManager.service.UnitService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UnitIngredientsTests {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private UnitService unitService;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    @DisplayName("Test findAll ingredients method")
    public void testFindAll() {
        // given
        List<Ingredient> expectedIngredients = new ArrayList<>();
        expectedIngredients.add(Ingredient.builder().id(1).name("Ingredient 1").build());
        expectedIngredients.add(Ingredient.builder().id(2).name("Ingredient 2").build());
        when(ingredientRepository.findAll()).thenReturn(expectedIngredients);

        // when
        List<Ingredient> actualIngredients = ingredientService.findAll();

        // then
        assertEquals(expectedIngredients, actualIngredients);
    }

    @Test
    @DisplayName("Test findByID ingredients method")
    public void testFindAllById() {
        // given
        List<Integer> ingredientIds = List.of(1, 2, 3);
        List<Ingredient> expectedIngredients = new ArrayList<>();
        expectedIngredients.add(Ingredient.builder().id(1).name("Ingredient 1").build());
        expectedIngredients.add(Ingredient.builder().id(2).name("Ingredient 2").build());
        when(ingredientRepository.findAllById(ingredientIds)).thenReturn(expectedIngredients);

        // when
        List<Ingredient> actualIngredients = ingredientService.findAllById(ingredientIds);

        // then
        assertEquals(expectedIngredients, actualIngredients);
    }

    @Test
    @DisplayName("Test findById method")
    public void testFindById() {
        // given
        Integer ingredientId = 1;
        Ingredient expectedIngredient = Ingredient.builder().id(ingredientId).name("Ingredient 1").build();
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(expectedIngredient));

        // when
        Optional<Ingredient> actualIngredient = ingredientService.findById(ingredientId);

        // then
        assertTrue(actualIngredient.isPresent());
        assertEquals(expectedIngredient, actualIngredient.get());
    }

    @Test
    @DisplayName("Test addIngredient method")
    public void testAddIngredient() {
        // given
        Integer unitId = 1;
        IngredientRequest request = IngredientRequest.builder().name("Ingredient 1").unitId(unitId).build();
        when(unitService.findById(unitId)).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(i -> i.getArgument(0));

        // when
        Ingredient actualIngredient = ingredientService.addIngredient(request);

        // then
        assertNotNull(actualIngredient);
        assertEquals(request.getName(), actualIngredient.getName());
        assertNull(actualIngredient.getUnit());
    }

    @Test
    @DisplayName("Test Delete method")
    public void testDeleteIngredient() {
        // given
        Integer ingredientId = 1;
        Ingredient existingIngredient = Ingredient.builder().id(ingredientId).name("Ingredient 1").build();
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(existingIngredient));

        // when
        boolean result = ingredientService.deleteIngredient(ingredientId);

        // then
        assertTrue(result);
        verify(ingredientRepository, times(1)).deleteById(ingredientId);
    }

    @Test
    @DisplayName("Test updateIngredient Unit method")
    public void testUpdateIngredientUnit() {
        // given
        Integer ingredientId = 1;
        Integer unitId = 2;
        Ingredient existingIngredient = Ingredient.builder().id(ingredientId).name("Ingredient 1").unit(Unit.builder().id(1).name("Unit 1").build()).build();
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(existingIngredient));
        when(unitService.findById(unitId)).thenReturn(Optional.of(Unit.builder().id(unitId).name("Unit 2").build()));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(i -> i.getArgument(0));

        // when
        IngredientRequest request = IngredientRequest.builder()
                .name(existingIngredient.getName())
                .unitId(unitId)
                .build();
        Ingredient updatedIngredient = ingredientService.updateIngredient(ingredientId, request).orElseThrow();

        // then
        assertNotNull(updatedIngredient);
        assertEquals(ingredientId, updatedIngredient.getId());
        assertEquals(existingIngredient.getName(), updatedIngredient.getName());
        assertEquals(unitId, updatedIngredient.getUnit().getId());
    }

    @Test
    @DisplayName("Test updateIngredient method")
    public void testUpdateIngredient() {
        // given
        Integer ingredientId = 1;
        Integer unitId = 2;
        IngredientRequest request = IngredientRequest.builder().name("Updated Ingredient").unitId(unitId).build();
        Ingredient existingIngredient = Ingredient.builder().id(ingredientId).name("Ingredient 1").unit(Unit.builder().id(unitId).name("Unit").build()).build();
        when(ingredientRepository.findById(ingredientId)).thenReturn(Optional.of(existingIngredient));
        when(unitService.findById(unitId)).thenReturn(Optional.of(Unit.builder().id(unitId).name("Updated Unit").build()));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(i -> i.getArgument(0));

        // when
        Ingredient updatedIngredient = ingredientService.updateIngredient(ingredientId, request).orElse(null);


        // then
        assertNotNull(updatedIngredient);
        assertEquals(ingredientId, updatedIngredient.getId());
        assertEquals(request.getName(), updatedIngredient.getName());
        assertEquals(unitId, updatedIngredient.getUnit().getId());
    }

}