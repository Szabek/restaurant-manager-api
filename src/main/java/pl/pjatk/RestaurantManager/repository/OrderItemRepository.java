package pl.pjatk.RestaurantManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.RestaurantManager.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}
