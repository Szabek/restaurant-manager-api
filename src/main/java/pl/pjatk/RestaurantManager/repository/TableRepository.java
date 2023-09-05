package pl.pjatk.RestaurantManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.RestaurantManager.model.Table;

public interface TableRepository extends JpaRepository<Table, Integer> {
}
