package pl.pjatk.RestaurantManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.RestaurantManager.model.Unit;

public interface UnitRepository extends JpaRepository<Unit, Integer> {
}
