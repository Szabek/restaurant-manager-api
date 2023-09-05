package pl.pjatk.RestaurantManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.RestaurantManager.model.Dish;

public interface DishRepository extends JpaRepository<Dish, Integer> {
}
