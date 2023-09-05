package pl.pjatk.RestaurantManager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.RestaurantManager.model.Order;
import pl.pjatk.RestaurantManager.model.Status;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> getOrdersByStatusIn(List<Status> statusList);
    List<Order> getOrderByStatusNot(Status status);
    List<Order> getOrdersByStatus(Status status);
    List<Order> findByStartDateTimeGreaterThanAndStatus(Timestamp startDate, Status status);
    List<Order> findByStartDateTimeBetweenAndStatus(Timestamp startDate,Timestamp endDate, Status status);
    List<Order> findByStartDateTimeBetween(Timestamp startOfDay, Timestamp endOfDay);
}
