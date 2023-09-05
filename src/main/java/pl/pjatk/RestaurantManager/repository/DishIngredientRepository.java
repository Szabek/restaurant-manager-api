package pl.pjatk.RestaurantManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.RestaurantManager.model.DishIngredient;

public interface DishIngredientRepository extends JpaRepository<DishIngredient, Integer> {
}
