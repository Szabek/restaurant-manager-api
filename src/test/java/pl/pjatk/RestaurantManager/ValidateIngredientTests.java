package pl.pjatk.RestaurantManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.pjatk.RestaurantManager.model.Ingredient;
import pl.pjatk.RestaurantManager.repository.IngredientRepository;
import pl.pjatk.RestaurantManager.request.IngredientRequest;
import pl.pjatk.RestaurantManager.service.IngredientService;
import pl.pjatk.RestaurantManager.service.UnitService;

public class ValidateIngredientTests {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private UnitService unitService;

    @InjectMocks
    private IngredientService ingredientService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("Salt");
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Sugar");
        List<Ingredient> ingredients = Arrays.asList(ingredient1, ingredient2);

        when(ingredientRepository.findAll()).thenReturn(ingredients);

        List<Ingredient> result = ingredientService.findAll();

        assertEquals(ingredients, result);
        verify(ingredientRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllById() {
        Integer id1 = 1;
        Integer id2 = 2;
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("Salt");
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Sugar");
        List<Integer> ids = Arrays.asList(id1, id2);
        List<Ingredient> ingredients = Arrays.asList(ingredient1, ingredient2);

        when(ingredientRepository.findAllById(ids)).thenReturn(ingredients);

        List<Ingredient> result = ingredientService.findAllById(ids);

        assertEquals(ingredients, result);
        verify(ingredientRepository, times(1)).findAllById(ids);
    }

    @Test
    public void testFindById() {
        Integer id = 1;
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Salt");
        Optional<Ingredient> optionalIngredient = Optional.of(ingredient);

        when(ingredientRepository.findById(id)).thenReturn(optionalIngredient);

        Optional<Ingredient> result = ingredientService.findById(id);

        assertEquals(optionalIngredient, result);
        verify(ingredientRepository, times(1)).findById(id);
    }

    @Test
    public void testAddIngredient() {
        IngredientRequest ingredientRequest = new IngredientRequest();
        ingredientRequest.setName("Salt");
        ingredientRequest.setUnitId(1);

        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientRequest.getName());

        when(unitService.findById(ingredientRequest.getUnitId())).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);

        Ingredient result = ingredientService.addIngredient(ingredientRequest);

        assertEquals(ingredient.getName(), result.getName());
        verify(unitService, times(1)).findById(ingredientRequest.getUnitId());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    public void testUpdateIngredient() {
        Integer id = 1;
        IngredientRequest ingredientRequest = new IngredientRequest();
        ingredientRequest.setName("Salt");
        ingredientRequest.setUnitId(1);

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Sugar");

        Ingredient updatedIngredient = new Ingredient();
        updatedIngredient.setName(ingredientRequest.getName());

        Optional<Ingredient> optionalIngredient = Optional.of(ingredient);

        when(ingredientRepository.findById(id)).thenReturn(optionalIngredient);
        when(unitService.findById(ingredientRequest.getUnitId())).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(updatedIngredient);

        Optional<Ingredient> result = ingredientService.updateIngredient(id, ingredientRequest);

        assertTrue(result.isPresent());
        assertEquals(updatedIngredient.getName(), result.get().getName());
        verify(ingredientRepository, times(1)).findById(id);
        verify(unitService, times(1)).findById(ingredientRequest.getUnitId());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    public void testUpdateIngredientNotFound() {
        Integer id = 1;
        IngredientRequest ingredientRequest = new IngredientRequest();
        ingredientRequest.setName("Salt");
        ingredientRequest.setUnitId(1);

        Optional<Ingredient> optionalIngredient = Optional.empty();

        when(ingredientRepository.findById(id)).thenReturn(optionalIngredient);

        Optional<Ingredient> result = ingredientService.updateIngredient(id, ingredientRequest);

        assertTrue(result.isEmpty());
        verify(ingredientRepository, times(1)).findById(id);
        verify(unitService, never()).findById(anyInt());
        verify(ingredientRepository, never()).save(any(Ingredient.class));
    }

    @Test
    public void testDeleteIngredient() {
        Integer id = 1;
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Salt");
        Optional<Ingredient> optionalIngredient = Optional.of(ingredient);

        when(ingredientRepository.findById(id)).thenReturn(optionalIngredient);

        boolean result = ingredientService.deleteIngredient(id);

        assertTrue(result);
        verify(ingredientRepository, times(1)).findById(id);
        verify(ingredientRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteIngredientNotFound() {
        Integer id = 1;
        Optional<Ingredient> optionalIngredient = Optional.empty();

        when(ingredientRepository.findById(id)).thenReturn(optionalIngredient);

        boolean result = ingredientService.deleteIngredient(id);
        assertFalse(result);
        verify(ingredientRepository, times(1)).findById(id);
        verify(ingredientRepository, times(0)).deleteById(id);
    }
}
