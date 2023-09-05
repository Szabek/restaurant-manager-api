package pl.pjatk.RestaurantManager.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.RestaurantManager.model.Dish;
import pl.pjatk.RestaurantManager.model.DishIngredient;
import pl.pjatk.RestaurantManager.model.Ingredient;
import pl.pjatk.RestaurantManager.repository.DishIngredientRepository;
import pl.pjatk.RestaurantManager.repository.DishRepository;
import pl.pjatk.RestaurantManager.request.DishRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DishService {
    private final DishRepository dishRepository;
    private final CategoryService categoryService;
    private final IngredientService ingredientService;
    private final DishIngredientRepository dishIngredientRepository;

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public List<Dish> findAllById(List<Integer> ids) {
        return dishRepository.findAllById(ids);
    }

    public Optional<Dish> findById(Integer id) {
        return dishRepository.findById(id);
    }

    public Dish addDish(DishRequest request) {
        var dish = Dish.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(categoryService.findById(request.getCategoryId()).orElse(null))
                .price(request.getPrice())
                .build();

        List<DishIngredient> dishIngredients = new ArrayList<>();
        for (int i = 0; i < request.getIngredientIds().size(); i++) {
            Integer ingredientId = request.getIngredientIds().get(i);
            Ingredient ingredient = ingredientService.findById(ingredientId).orElse(null);
            if (ingredient != null) {
                Double quantity = 1.0; // Domyślna ilość dla danego składnika
                if (request.getIngredientQuantities() != null && i < request.getIngredientQuantities().size()) {
                    quantity = request.getIngredientQuantities().get(i);
                }
                DishIngredient dishIngredient = DishIngredient.builder()
                        .dish(dish)
                        .ingredient(ingredient)
                        .quantity(quantity)
                        .build();
                dishIngredients.add(dishIngredient);
            }
        }

        dish.setDishIngredients(dishIngredients);

        try {
            byte[] imageBytes = request.getImage().getBytes();
            String imageName = request.getImage().getOriginalFilename();
            Path imagePath = Paths.get("uploads/" + imageName);
            Files.write(imagePath, imageBytes);
            dish.setImage(imageName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dishRepository.save(dish);
    }

    public Dish updateDish(Integer dishId, DishRequest request) {
        var dish = dishRepository.findById(dishId).orElseThrow(() -> new EntityNotFoundException("Dish not found"));
        dish.setName(request.getName());
        dish.setDescription(request.getDescription());
        dish.setCategory(categoryService.findById(request.getCategoryId()).orElse(null));
        dish.setPrice(request.getPrice());

        List<DishIngredient> updatedDishIngredients = new ArrayList<>();
        List<DishIngredient> ingredientsToRemove = new ArrayList<>();

        for (int i = 0; i < request.getIngredientIds().size(); i++) {
            Integer ingredientId = request.getIngredientIds().get(i);
            Ingredient ingredient = ingredientService.findById(ingredientId).orElse(null);
            if (ingredient != null) {
                Double quantity = 1.0; // Domyślna ilość dla danego składnika
                if (request.getIngredientQuantities() != null && i < request.getIngredientQuantities().size()) {
                    quantity = request.getIngredientQuantities().get(i);
                }
                DishIngredient dishIngredient = DishIngredient.builder()
                        .dish(dish)
                        .ingredient(ingredient)
                        .quantity(quantity)
                        .build();
                updatedDishIngredients.add(dishIngredient);
            }
        }

        for (DishIngredient dishIngredient : dish.getDishIngredients()) {
            if (!updatedDishIngredients.contains(dishIngredient)) {
                ingredientsToRemove.add(dishIngredient);
            }
        }

        for (DishIngredient dishIngredient : ingredientsToRemove) {
            dish.getDishIngredients().remove(dishIngredient);
            dishIngredient.getDish().getDishIngredients().remove(dishIngredient);
            dishIngredientRepository.delete(dishIngredient);
        }

        dish.getDishIngredients().addAll(updatedDishIngredients);

        try {
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                byte[] imageBytes = request.getImage().getBytes();
                String imageName = request.getImage().getOriginalFilename();
                Path imagePath = Paths.get("uploads/" + imageName);
                Files.write(imagePath, imageBytes);
                dish.setImage(imageName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dishRepository.save(dish);
    }

    public boolean deleteDish(Integer id) {
        if (dishRepository.findById(id).isEmpty()) {
            return false;
        }

        dishRepository.deleteById(id);
        return true;
    }
}
