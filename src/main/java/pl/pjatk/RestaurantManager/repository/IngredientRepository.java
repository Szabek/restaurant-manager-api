package pl.pjatk.RestaurantManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.RestaurantManager.model.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

}
