package pl.pjatk.RestaurantManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.RestaurantManager.service.StatisticService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
@CrossOrigin()
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/order-count-statistics")
    public ResponseEntity<Map<String, Integer>> getOrderCountStatistics(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Integer> orderCountStatistics = statisticService.calculateOrderCountStatistics(startDate, endDate);
        return ResponseEntity.ok(orderCountStatistics);
    }

    @GetMapping("/total-price-statistics")
    public ResponseEntity<Map<String, BigDecimal>> getTotalPriceStatistics(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, BigDecimal> totalPriceStatistics = statisticService
                .calculateTotalPriceStatistics(startDate, endDate);
        return ResponseEntity.ok(totalPriceStatistics);
    }

    @GetMapping("/waiter-statistics")
    public ResponseEntity<Map<String, Integer>> getWaiterStatistics(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Integer> statistics = statisticService.calculateWaiterStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/dish-statistics")
    public ResponseEntity<Map<String, Integer>> getDishStatistics(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Integer> statistics = statisticService.calculateDishStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/ingredient-statistics")
    public ResponseEntity<Map<String, Double>> getIngredientStatistics(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Double> statistics = statisticService.calculateIngredientStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/traffic-statistics")
    public ResponseEntity<Map<Integer, Integer>> getOrderCountByHour(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<Integer, Integer> orderCountByHour = statisticService.calculateOrderCountByHour(date);
        return ResponseEntity.ok(orderCountByHour);
    }
}
