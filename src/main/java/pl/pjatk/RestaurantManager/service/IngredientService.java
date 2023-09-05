package pl.pjatk.RestaurantManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.RestaurantManager.model.Ingredient;
import pl.pjatk.RestaurantManager.repository.IngredientRepository;
import pl.pjatk.RestaurantManager.request.IngredientRequest;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final UnitService unitService;

    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }
    public List<Ingredient> findAllById(List<Integer> ids) {
        return ingredientRepository.findAllById(ids);
    }

    public Optional<Ingredient> findById(Integer id) {
        return ingredientRepository.findById(id);
    }

    public Ingredient addIngredient(IngredientRequest request) {
        var ingredient = Ingredient.builder()
                .name(request.getName())
                .unit(unitService.findById(request.getUnitId()).orElse(null))
                .build();
        return ingredientRepository.save(ingredient);
    }

    public Optional<Ingredient> updateIngredient(Integer id, IngredientRequest request) {
        return ingredientRepository.findById(id)
                .map(ingredient -> {
                    ingredient.setName(request.getName());
                    ingredient.setUnit(unitService.findById(request.getUnitId()).orElse(null));
                    return ingredientRepository.save(ingredient);
                });
    }

    public boolean deleteIngredient(Integer id) {
        if (ingredientRepository.findById(id).isEmpty()) {
            return false;
        }

        ingredientRepository.deleteById(id);
        return true;
    }
}
