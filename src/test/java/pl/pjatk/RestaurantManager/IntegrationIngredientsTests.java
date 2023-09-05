package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.RestaurantManager.model.Ingredient;
import pl.pjatk.RestaurantManager.model.Unit;
import pl.pjatk.RestaurantManager.repository.IngredientRepository;
import pl.pjatk.RestaurantManager.repository.UnitRepository;
import pl.pjatk.RestaurantManager.request.IngredientRequest;
import pl.pjatk.RestaurantManager.service.IngredientService;
import pl.pjatk.RestaurantManager.service.UnitService;


import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class IntegrationIngredientsTests {
    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UnitService unitService;

    @Autowired
    private UnitRepository unitRepository;

    @Test
    public void testFindAll() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");
        unitRepository.save(unit);

        Ingredient ingredient = new Ingredient();
        ingredient.setName("flour");
        ingredient.setUnit(unit);
        ingredientRepository.save(ingredient);

        // when
        List<Ingredient> ingredients = ingredientService.findAll();

        // then
        Assertions.assertFalse(ingredients.isEmpty());
    }


    @Test
    public void testFindAllById() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");
        unitRepository.save(unit);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("flour");
        ingredient1.setUnit(unit);
        ingredientRepository.save(ingredient1);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("water");
        ingredient2.setUnit(unit);
        ingredientRepository.save(ingredient2);

        // when
        List<Ingredient> ingredients = ingredientService.findAllById(List.of(ingredient1.getId(), ingredient2.getId()));

        // then
        Assertions.assertEquals(2, ingredients.size());
    }

    @Test
    public void testFindById() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");
        unitRepository.save(unit);

        Ingredient ingredient = new Ingredient();
        ingredient.setName("flour");
        ingredient.setUnit(unit);
        ingredientRepository.save(ingredient);

        // when
        Optional<Ingredient> foundIngredient = ingredientService.findById(ingredient.getId());

        // then
        Assertions.assertTrue(foundIngredient.isPresent());
        Assertions.assertEquals("flour", foundIngredient.get().getName());
        Assertions.assertEquals(unit.getId(), foundIngredient.get().getUnit().getId());
    }

    @Test
    public void testAddIngredient() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");
        unitRepository.save(unit);

        IngredientRequest ingredientRequest = new IngredientRequest();
        ingredientRequest.setName("flour");
        ingredientRequest.setUnitId(unit.getId());

        // when
        Ingredient savedIngredient = ingredientService.addIngredient(ingredientRequest);

        // then
        Assertions.assertNotNull(savedIngredient.getId());
        Assertions.assertEquals("flour", savedIngredient.getName());
        Assertions.assertEquals(unit.getId(), savedIngredient.getUnit().getId());
    }

    @Test
    public void testUpdateIngredient() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");
        unitRepository.save(unit);

        Ingredient ingredient = new Ingredient();
        ingredient.setName("flour");
        ingredient.setUnit(unit);
        ingredientRepository.save(ingredient);

        IngredientRequest updatedIngredientRequest = new IngredientRequest();
        updatedIngredientRequest.setName("sugar");
        updatedIngredientRequest.setUnitId(unit.getId());
        // when
        Optional<Ingredient> updatedIngredient = ingredientService.updateIngredient(ingredient.getId(), updatedIngredientRequest);

        // then
        Assertions.assertTrue(updatedIngredient.isPresent());
        Assertions.assertEquals("sugar", updatedIngredient.get().getName());
        Assertions.assertEquals(unit.getId(), updatedIngredient.get().getUnit().getId());
    }
    @Test
    public void testDeleteIngredient() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");
        unitRepository.save(unit);

        Ingredient ingredient = new Ingredient();
        ingredient.setName("flour");
        ingredient.setUnit(unit);
        ingredient = ingredientRepository.save(ingredient);

        // when
        boolean result = ingredientService.deleteIngredient(ingredient.getId());

        // then
        Assertions.assertTrue(result);
        Assertions.assertFalse(ingredientRepository.existsById(ingredient.getId()));
    }
}