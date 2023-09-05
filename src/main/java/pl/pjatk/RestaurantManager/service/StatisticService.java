package pl.pjatk.RestaurantManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.RestaurantManager.model.Order;
import pl.pjatk.RestaurantManager.model.OrderItem;
import pl.pjatk.RestaurantManager.model.Status;
import pl.pjatk.RestaurantManager.repository.OrderRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final OrderRepository orderRepository;

    public Map<String, Integer> calculateOrderCountStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findByStartDateTimeBetweenAndStatus(Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), Status.CLOSED);

        Map<String, Integer> orderCountStatistics = new LinkedHashMap<>();

        for (Order order : orders) {
            LocalDateTime orderDateTime = order.getStartDateTime().toLocalDateTime();
            LocalDate orderDate = orderDateTime.toLocalDate();

            String formattedDate = orderDate.toString();
            int orderCount = orderCountStatistics.getOrDefault(formattedDate, 0);
            orderCountStatistics.put(formattedDate, orderCount + 1);
        }

        return orderCountStatistics;
    }

    public Map<String, BigDecimal> calculateTotalPriceStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findByStartDateTimeBetweenAndStatus(Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), Status.CLOSED);

        Map<String, BigDecimal> totalPriceStatistics = new LinkedHashMap<>();

        for (Order order : orders) {
            LocalDateTime orderDateTime = order.getStartDateTime().toLocalDateTime();
            LocalDate orderDate = orderDateTime.toLocalDate();

            String formattedDate = orderDate.toString();
            BigDecimal totalPrice = totalPriceStatistics.getOrDefault(formattedDate, BigDecimal.ZERO);
            totalPrice = totalPrice.add(order.getTotalPrice());
            totalPriceStatistics.put(formattedDate, totalPrice);
        }

        return totalPriceStatistics;
    }

    public LinkedHashMap<String, Integer> calculateWaiterStatistics(LocalDate startDate, LocalDate endDate) {
        LinkedHashMap<String, Integer> statistics = new LinkedHashMap<>();

        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        List<Order> orders = orderRepository.findByStartDateTimeBetweenAndStatus(Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), Status.CLOSED);
        for (Order order : orders) {
            String waiterName = order.getUser().getFirstname() + " " + order.getUser().getLastname();
            int orderCount = statistics.getOrDefault(waiterName, 0);
            statistics.put(waiterName, orderCount + 1);
        }

        return statistics;
    }

    public LinkedHashMap<String, Integer> calculateDishStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        List<Order> orders = orderRepository.findByStartDateTimeBetweenAndStatus(Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), Status.CLOSED);

        LinkedHashMap<String, Integer> dishStatistics = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.groupingBy(orderItem -> orderItem.getDish().getName(),
                        LinkedHashMap::new,
                        Collectors.summingInt(OrderItem::getQuantity)));

        return dishStatistics;
    }

    public LinkedHashMap<String, Double> calculateIngredientStatistics(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        List<Order> orders = orderRepository.findByStartDateTimeBetweenAndStatus(Timestamp.valueOf(startDateTime), Timestamp.valueOf(endDateTime), Status.CLOSED);

        LinkedHashMap<String, Double> ingredientQuantities = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .flatMap(orderItem -> orderItem.getDish().getDishIngredients().stream()
                        .filter(dishIngredient -> dishIngredient.getIngredient() != null)
                        .map(dishIngredient -> {
                            String ingredientName = dishIngredient.getIngredient().getName();
                            String ingredientUnit = dishIngredient.getIngredient().getUnit().getName();
                            String key = ingredientName + " [" + ingredientUnit + "]";
                            double quantity = dishIngredient.getQuantity() * orderItem.getQuantity();
                            return new AbstractMap.SimpleEntry<>(key, quantity);
                        }))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                        LinkedHashMap::new,
                        Collectors.summingDouble(AbstractMap.SimpleEntry::getValue)));

        return ingredientQuantities;
    }

    public Map<Integer, Integer> calculateOrderCountByHour(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        Timestamp startOfDayTimestamp = Timestamp.valueOf(startOfDay);
        Timestamp endOfDayTimestamp = Timestamp.valueOf(endOfDay);

        List<Order> orders = orderRepository.findByStartDateTimeBetween(startOfDayTimestamp, endOfDayTimestamp);

        Map<Integer, Integer> orderCountByHour = new HashMap<>();

        for (Order order : orders) {
            LocalDateTime orderDateTime = order.getStartDateTime().toLocalDateTime();
            int hour = orderDateTime.getHour();
            orderCountByHour.put(hour, orderCountByHour.getOrDefault(hour, 0) + 1);
        }

        for (int hour = 1; hour <= 24; hour++) {
            orderCountByHour.putIfAbsent(hour, 0);
        }

        return orderCountByHour;
    }
}
