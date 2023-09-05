package pl.pjatk.RestaurantManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.RestaurantManager.exceptions.ResourceNotFoundException;
import pl.pjatk.RestaurantManager.model.Dish;
import pl.pjatk.RestaurantManager.model.Order;
import pl.pjatk.RestaurantManager.model.OrderItem;
import pl.pjatk.RestaurantManager.model.Status;
import pl.pjatk.RestaurantManager.repository.OrderItemRepository;
import pl.pjatk.RestaurantManager.repository.OrderRepository;
import pl.pjatk.RestaurantManager.repository.UserRepository;
import pl.pjatk.RestaurantManager.request.OrderCreateRequest;
import pl.pjatk.RestaurantManager.request.OrderItemRequest;
import pl.pjatk.RestaurantManager.request.OrderUpdateRequest;

import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TableService tableService;
    private final DishService dishService;
    private final OrderItemRepository orderItemRepository;


    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findAllNotClosed() {
        return orderRepository.getOrderByStatusNot(Status.CLOSED);
    }

    public List<Order> findOrdersByStatus(Status status) {
        return orderRepository.getOrdersByStatus(status);
    }

    public List<Order> findOrdersInRange(LocalDate startDate, LocalDate endDate, Status status) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        return orderRepository.findByStartDateTimeBetweenAndStatus(Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), status);
    }

    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    public Order addOrder(OrderCreateRequest request) {
        var order = Order.builder()
                .user(userRepository.findById(request.getUserId()).get())
                .table(tableService.findById(request.getTableId()).get())
                .status(Status.OPEN)
                .build();
        return orderRepository.save(order);
    }

    public Optional<Order> updateOrder(Integer id, OrderUpdateRequest request) {
        return orderRepository.findById(id).map(order -> {
            order.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found")));
            order.setTable(tableService.findById(request.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found")));
            if (request.getIsStartedProcessing() && order.getOrderProcessingStart() == null) {
                order.setOrderProcessingStart(new Timestamp(System.currentTimeMillis()));
            }
            order.setStatus(request.getStatus());
            order.setIsReadyToServe(request.getIsReadyToServe());
            order.setTotalPrice(request.getTotalPrice());
            if (request.getStatus() == Status.CLOSED) {
                Duration duration = Duration.between(order.getStartDateTime().toInstant(), Instant.now());
                order.setDuration(duration);
                tableService.updateIsOccupied(request.getTableId(), false);
            } else {
                order.setDuration(null);
            }

            List<OrderItem> updatedOrderItems = new ArrayList<>();
            List<OrderItem> itemsToRemove = new ArrayList<>();

            for (OrderItemRequest itemRequest : request.getOrderItems()) {
                Dish dish = dishService.findById(itemRequest.getDishId())
                        .orElseThrow(() -> new ResourceNotFoundException("Dish not found"));

                OrderItem orderItem = new OrderItem();
                orderItem.setDish(dish);
                orderItem.setOrder(order);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setReady(itemRequest.getReady());
                updatedOrderItems.add(orderItem);
            }

            for (OrderItem orderItem : order.getOrderItems()) {
                if (!updatedOrderItems.contains(orderItem)) {
                    itemsToRemove.add(orderItem);
                }
            }

            for (OrderItem orderItem : itemsToRemove) {
                order.getOrderItems().remove(orderItem);
                orderItem.getOrder().getOrderItems().remove(orderItem);
                orderItemRepository.delete(orderItem);
            }

            order.setOrderItems(updatedOrderItems);

            return orderRepository.save(order);
        });
    }

    public Optional<Order> setReadyToServe(Integer id, Boolean isReady) {
        return orderRepository.findById(id).map(order -> {
            order.setIsReadyToServe(isReady);
            return orderRepository.save(order);
        });
    }

    public Optional<List<Map<String, Object>>> getOpenOrderServeStatus() {
        List<Order> orderList = orderRepository.getOrderByStatusNot(Status.CLOSED);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Order order : orderList) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("status", order.getStatus());
            orderMap.put("isReadyToServe", order.getIsReadyToServe());
            orderMap.put("tableId", order.getTable().getId());
            result.add(orderMap);
        }

        return Optional.of(result);
    }

    public boolean deleteOrder(Integer id) {
        if (orderRepository.findById(id).isEmpty()) {
            return false;
        }

        orderRepository.deleteById(id);
        return true;
    }
}
