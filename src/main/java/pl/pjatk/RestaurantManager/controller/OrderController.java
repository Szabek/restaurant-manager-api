package pl.pjatk.RestaurantManager.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.RestaurantManager.dto.OrderUpdateDto;
import pl.pjatk.RestaurantManager.model.Order;
import pl.pjatk.RestaurantManager.model.Status;
import pl.pjatk.RestaurantManager.request.OrderCreateRequest;
import pl.pjatk.RestaurantManager.request.OrderUpdateRequest;
import pl.pjatk.RestaurantManager.service.OrderService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@CrossOrigin()
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @GetMapping("/all")
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/all-opened")
    public ResponseEntity<List<Order>> findAllOpened() {
        return ResponseEntity.ok(orderService.findAllNotClosed());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Integer id) {
        Optional<Order> orderById = orderService.findById(id);

        return orderById.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/status")
    public ResponseEntity<List<OrderUpdateDto>> findByStatus(@RequestParam String status) {
        List<Order> orders = orderService.findOrdersByStatus(Status.valueOf(status));

        return ResponseEntity.ok(orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/range")
    public ResponseEntity<List<OrderUpdateDto>> findOrdersInRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("status") String status) {
        List<Order> ordersInRange = orderService.findOrdersInRange(startDate, endDate, Status.valueOf(status));
        return ResponseEntity.ok(ordersInRange.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("serve-status")
    public Optional<List<Map<String, Object>>> getOpenOrderServeStatus() {
        return orderService.getOpenOrderServeStatus();
    }

    @PreAuthorize("hasAnyAuthority('WAITER')")
    @PostMapping("/add")
    public ResponseEntity<Order> addOrder(@RequestBody @Valid OrderCreateRequest request) {
        return ResponseEntity.ok(orderService.addOrder(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OrderUpdateDto> updateOrder(@PathVariable Integer id, @RequestBody OrderUpdateRequest request) {
        return ResponseEntity.ok(convertToDto(orderService.updateOrder(id, request).get()));
    }

    @PutMapping("/set-ready/{id}")
    public ResponseEntity<OrderUpdateDto> setReadyToServe(@PathVariable Integer id, @RequestParam Boolean isReady) {
        return ResponseEntity.ok(convertToDto(orderService.setReadyToServe(id, isReady).get()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.deleteOrder(id));
    }

    private OrderUpdateDto convertToDto(Order order) {
        OrderUpdateDto orderUpdateDto = modelMapper.map(order, OrderUpdateDto.class);
        orderUpdateDto.setId(order.getId());
        orderUpdateDto.setUserId(order.getUser().getId());
        orderUpdateDto.setUserName(order.getUser().getFirstname() + ' ' + order.getUser().getLastname());
        orderUpdateDto.setTable(order.getTable());
        orderUpdateDto.setOrderItems(order.getOrderItems());
        orderUpdateDto.setStartDateTime(order.getStartDateTime());
        orderUpdateDto.setOrderProcessingStart(order.getOrderProcessingStart());
        orderUpdateDto.setStatus(order.getStatus());
        orderUpdateDto.setIsReadyToServe(order.getIsReadyToServe());
        orderUpdateDto.setTotalPrice(order.getTotalPrice());
        orderUpdateDto.setDuration(order.getDuration());

        return orderUpdateDto;
    }
}
