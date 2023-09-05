package pl.pjatk.RestaurantManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.RestaurantManager.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
