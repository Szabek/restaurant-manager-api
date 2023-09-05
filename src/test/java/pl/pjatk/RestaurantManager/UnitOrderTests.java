package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.pjatk.RestaurantManager.exceptions.ResourceNotFoundException;
import pl.pjatk.RestaurantManager.model.*;
import pl.pjatk.RestaurantManager.repository.OrderItemRepository;
import pl.pjatk.RestaurantManager.repository.OrderRepository;
import pl.pjatk.RestaurantManager.repository.UserRepository;
import pl.pjatk.RestaurantManager.request.OrderCreateRequest;
import pl.pjatk.RestaurantManager.request.OrderItemRequest;
import pl.pjatk.RestaurantManager.request.OrderUpdateRequest;
import pl.pjatk.RestaurantManager.service.DishService;
import pl.pjatk.RestaurantManager.service.OrderService;
import pl.pjatk.RestaurantManager.service.TableService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UnitOrderTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TableService tableService;

    @Mock
    private DishService dishService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Table table;
    private Dish dish;
    private Order order;
    private OrderCreateRequest orderCreateRequest;
    private OrderUpdateRequest orderUpdateRequest;
    private OrderItemRequest orderItemRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        /*user.setUsername("JohnDoe");*/

        table = new Table();
        table.setId(1);
        table.setName("Table 1");
        table.setSeatsNumber(4);
        table.setIsActive(true);
        table.setIsOccupied(false);

        dish = new Dish();
        dish.setId(1);
        dish.setName("Dish 1");
        dish.setDescription("Description 1");
        dish.setPrice(BigDecimal.valueOf(10.0));

        order = new Order();
        order.setId(1);
        order.setUser(user);
        order.setTable(table);
        order.setStatus(Status.OPEN);
        order.setIsReadyToServe(false);

        orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setUserId(1);
        orderCreateRequest.setTableId(1);

        orderUpdateRequest = new OrderUpdateRequest();
        orderUpdateRequest.setUserId(1);
        orderUpdateRequest.setTableId(1);
        orderUpdateRequest.setIsStartedProcessing(true);
        orderUpdateRequest.setStatus(Status.CLOSED);
        orderUpdateRequest.setIsReadyToServe(true);
        orderUpdateRequest.setTotalPrice(BigDecimal.valueOf(50.0));

        orderItemRequest = new OrderItemRequest();
        orderItemRequest.setDishId(1);
        orderItemRequest.setQuantity(2);
        orderItemRequest.setReady(false);
    }

    @Test
    @DisplayName("Test findAll method")
    public void testFindAll() {
        // Given
        List<Order> orders = Collections.singletonList(order);
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        List<Order> result = orderService.findAll();

        // Then
        assertEquals(orders, result);
    }

    @Test
    @DisplayName("Test findAllNotClosed method")
    public void testFindAllNotClosed() {
        // Given
        List<Order> orders = Collections.singletonList(order);
        when(orderRepository.getOrderByStatusNot(Status.CLOSED)).thenReturn(orders);

        // When
        List<Order> result = orderService.findAllNotClosed();

        // Then
        assertEquals(orders, result);
    }

    @Test
    @DisplayName("Test findOrdersByStatus method")
    public void testFindOrdersByStatus() {
        // Given
        List<Order> orders = Collections.singletonList(order);
        when(orderRepository.getOrdersByStatus(Status.OPEN)).thenReturn(orders);

        // When
        List<Order> result = orderService.findOrdersByStatus(Status.OPEN);

        // Then
        assertEquals(orders, result);
    }

    @Test
    @DisplayName("Test findById method with existing id")
    public void testFindById() {
        // Given
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        // When
        Optional<Order> result = orderService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(order, result.get());
    }

    @Test
    @DisplayName("Test findById method with non-existent id")
    public void testFindByIdNotFound() {
        // Given
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderService.findById(1);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test addOrder method")
    public void testAddOrder() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tableService.findById(1)).thenReturn(Optional.of(table));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        Order result = orderService.addOrder(orderCreateRequest);

        // Then
        assertEquals(order, result);
        assertEquals(user, result.getUser());
        assertEquals(table, result.getTable());
        assertEquals(Status.OPEN, result.getStatus());
        assertFalse(result.getIsReadyToServe());
    }


    @Test
    @DisplayName("Test updateOrder method with non-existent id")
    public void testUpdateOrderNotFound() {
        // Given
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderService.updateOrder(1, orderUpdateRequest);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test setReadyToServe method with valid id and isReady value")
    public void testSetReadyToServe() {
        // Given
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        Optional<Order> result = orderService.setReadyToServe(1, true);

        // Then
        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        assertTrue(result.get().getIsReadyToServe());
    }

    @Test
    @DisplayName("Test setReadyToServe method with non-existent id")
    public void testSetReadyToServeNotFound() {
        // Given
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderService.setReadyToServe(1, true);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test getOpenOrderServeStatus method")
    public void testGetOpenOrderServeStatus() {
        // Given
        List<Order> orders = Collections.singletonList(order);
        when(orderRepository.getOrderByStatusNot(Status.CLOSED)).thenReturn(orders);

        // When
        Optional<List<Map<String, Object>>> result = orderService.getOpenOrderServeStatus();

        // Then
        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        Map<String, Object> orderMap = result.get().get(0);
        assertEquals(1, orderMap.get("id"));
        assertEquals(Status.OPEN, orderMap.get("status"));
        assertFalse((boolean) orderMap.get("isReadyToServe"));
        assertEquals(1, orderMap.get("tableId"));
    }

    @Test
    @DisplayName("Test deleteOrder method with existing id")
    public void testDeleteOrder() {
        // Given
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        // When
        boolean result = orderService.deleteOrder(1);

        // Then
        assertTrue(result);
        verify(orderRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Test deleteOrder method with non-existent id")
    public void testDeleteOrderNotFound() {
        // Given
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // When
        boolean result = orderService.deleteOrder(1);

        // Then
        assertFalse(result);
        verify(orderRepository, never()).deleteById(anyInt());
    }
}