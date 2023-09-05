package pl.pjatk.RestaurantManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

public class ValidateOrderTests {

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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> orders = List.of(order1, order2);

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.findAll();

        assertEquals(orders, result);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllNotClosed() {
        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> orders = List.of(order1, order2);

        when(orderRepository.getOrderByStatusNot(Status.CLOSED)).thenReturn(orders);

        List<Order> result = orderService.findAllNotClosed();

        assertEquals(orders, result);
        verify(orderRepository, times(1)).getOrderByStatusNot(Status.CLOSED);
    }

    @Test
    public void testFindOrdersByStatus() {
        Status status = Status.OPEN;
        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> orders = List.of(order1, order2);

        when(orderRepository.getOrdersByStatus(status)).thenReturn(orders);

        List<Order> result = orderService.findOrdersByStatus(status);

        assertEquals(orders, result);
        verify(orderRepository, times(1)).getOrdersByStatus(status);
    }

    @Test
    public void testFindById() {
        Integer id = 1;
        Order order = new Order();
        Optional<Order> optionalOrder = Optional.of(order);

        when(orderRepository.findById(id)).thenReturn(optionalOrder);

        Optional<Order> result = orderService.findById(id);

        assertEquals(optionalOrder, result);
        verify(orderRepository, times(1)).findById(id);
    }
    @Test
    public void testAddOrder() {
        Integer userId = 1;
        Integer tableId = 1;
        OrderCreateRequest request = new OrderCreateRequest();
        request.setUserId(userId);
        request.setTableId(tableId);

        Order order = new Order();
        order.setUser(new User()); // Set user as needed
        order.setTable(new Table()); // Set table as needed

        when(userRepository.findById(userId)).thenReturn(Optional.of(order.getUser()));
        when(tableService.findById(tableId)).thenReturn(Optional.of(order.getTable()));
        when(orderRepository.save(any(Order.class))).thenReturn(order); // Use any(Order.class) matcher

        Order result = orderService.addOrder(request);

        assertNotNull(result); // Verify that result is not null
        assertEquals(order, result);
        verify(userRepository, times(1)).findById(userId);
        verify(tableService, times(1)).findById(tableId);
        verify(orderRepository, times(1)).save(any(Order.class)); // Use any(Order.class) matcher
    }


    @Test
    public void testUpdateOrder() {
        Integer id = 1;
        OrderUpdateRequest request = new OrderUpdateRequest();
        request.setUserId(1);
        request.setTableId(1);
        request.setIsStartedProcessing(true);
        request.setStatus(Status.CLOSED);
        request.setTotalPrice(BigDecimal.valueOf(10.0));

        List<OrderItemRequest> orderItems = List.of(new OrderItemRequest());
        orderItems.get(0).setQuantity(1);
        orderItems.get(0).setReady(Boolean.TRUE); // Ustawienie wartości gotowości
        request.setOrderItems(orderItems);

        Order order = new Order();
        order.setStartDateTime(Timestamp.from(Instant.now()));
        order.setUser(new User());
        order.setTable(new Table());
        order.setOrderItems(new ArrayList<>());

        Optional<Order> optionalOrder = Optional.of(order);

        Dish dish = new Dish();

        when(orderRepository.findById(id)).thenReturn(optionalOrder);
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(order.getUser()));
        when(tableService.findById(request.getTableId())).thenReturn(Optional.of(order.getTable()));
        when(dishService.findById(any())).thenReturn(Optional.of(dish));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Order> result = orderService.updateOrder(id, request);

        assertTrue(result.isPresent());

        assertEquals(order, result.get());
        verify(orderRepository, times(1)).findById(id);
        verify(userRepository, times(1)).findById(request.getUserId());
        verify(tableService, times(1)).findById(request.getTableId());
        verify(dishService, times(orderItems.size())).findById(any());
        verify(orderRepository, times(1)).save(order);
    }


    @Test
    public void testUpdateOrderNotFound() {
        Integer id = 1;
        OrderUpdateRequest request = new OrderUpdateRequest();
        request.setUserId(1);
        request.setTableId(1);
        request.setIsStartedProcessing(true);
        request.setStatus(Status.CLOSED);
        request.setIsReadyToServe(true);
        request.setTotalPrice(BigDecimal.valueOf(10.0));
        List<OrderItemRequest> orderItems = List.of(new OrderItemRequest());
        request.setOrderItems(orderItems);

        Optional<Order> optionalOrder = Optional.empty();

        when(orderRepository.findById(id)).thenReturn(optionalOrder);

        Optional<Order> result = orderService.updateOrder(id, request);

        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findById(id);
        verify(userRepository, never()).findById(any());
        verify(tableService, never()).findById(any());
        verify(dishService, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    public void testSetReadyToServe() {
        Integer id = 1;
        Boolean isReady = true;
        Order order = new Order();

        Optional<Order> optionalOrder = Optional.of(order);

        when(orderRepository.findById(id)).thenReturn(optionalOrder);
        when(orderRepository.save(order)).thenReturn(order);

        Optional<Order> result = orderService.setReadyToServe(id, isReady);

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        assertEquals(isReady, order.getIsReadyToServe());
        verify(orderRepository, times(1)).findById(id);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void testSetReadyToServeNotFound() {
        Integer id = 1;
        Boolean isReady = true;
        Optional<Order> optionalOrder = Optional.empty();

        when(orderRepository.findById(id)).thenReturn(optionalOrder);

        Optional<Order> result = orderService.setReadyToServe(id, isReady);

        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findById(id);
        verify(orderRepository, never()).save(any());
    }

    @Test
    public void testGetOpenOrderServeStatus() {
        Order order = new Order();
        order.setId(1);
        order.setStatus(Status.OPEN);
        order.setIsReadyToServe(false);
        order.setTable(new Table());
        List<Order> orders = Collections.singletonList(order);

        when(orderRepository.getOrderByStatusNot(Status.CLOSED)).thenReturn(orders);

        Optional<List<Map<String, Object>>> result = orderService.getOpenOrderServeStatus();

        assertTrue(result.isPresent());
        List<Map<String, Object>> expectedResult = new ArrayList<>();
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", order.getId());
        orderMap.put("status", order.getStatus());
        orderMap.put("isReadyToServe", order.getIsReadyToServe());
        orderMap.put("tableId", order.getTable().getId());
        expectedResult.add(orderMap);
        assertEquals(expectedResult, result.get());
        verify(orderRepository, times(1)).getOrderByStatusNot(Status.CLOSED);
    }

    @Test
    public void testDeleteOrder() {
        Integer id = 1;
        Order order = new Order();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        boolean result = orderService.deleteOrder(id);

        assertTrue(result);
        verify(orderRepository, times(1)).findById(id);
        verify(orderRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteOrderNotFound() {
        Integer id = 1;

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = orderService.deleteOrder(id);

        assertFalse(result);
        verify(orderRepository, times(1)).findById(id);
        verify(orderRepository, times(0)).deleteById(id);
    }
}
