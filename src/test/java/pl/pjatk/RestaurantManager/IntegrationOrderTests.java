package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.pjatk.RestaurantManager.exceptions.ResourceNotFoundException;
import pl.pjatk.RestaurantManager.model.*;
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
import static org.mockito.Mockito.*;

@SpringBootTest
public class IntegrationOrderTests {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TableService tableService;

    @MockBean
    private DishService dishService;

    @Test
    public void testFindAllOrders() {
        // given
        List<Order> orders = Arrays.asList(
                createSampleOrder(1),
                createSampleOrder(2)
        );
        when(orderRepository.findAll()).thenReturn(orders);

        // when
        List<Order> result = orderService.findAll();

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(orders.get(0)));
        assertTrue(result.contains(orders.get(1)));
    }

    @Test
    public void testFindAllNotClosedOrders() {
        // given
        List<Order> orders = Arrays.asList(
                createSampleOrder(1),
                createSampleOrder(2)
        );
        when(orderRepository.getOrderByStatusNot(Status.CLOSED)).thenReturn(orders);

        // when
        List<Order> result = orderService.findAllNotClosed();

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(orders.get(0)));
        assertTrue(result.contains(orders.get(1)));
    }

    @Test
    public void testFindOrdersByStatus() {
        // given
        List<Order> orders = Arrays.asList(
                createSampleOrder(1),
                createSampleOrder(2)
        );
        when(orderRepository.getOrdersByStatus(Status.IN_PROGRESS)).thenReturn(orders);

        // when
        List<Order> result = orderService.findOrdersByStatus(Status.IN_PROGRESS);

        // then
        assertEquals(2, result.size());
        assertTrue(result.contains(orders.get(0)));
        assertTrue(result.contains(orders.get(1)));
    }

    @Test
    public void testFindOrderById() {
        // given
        Order order = createSampleOrder(1);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        // when
        Optional<Order> result = orderService.findById(1);

        // then
        assertTrue(result.isPresent());
        assertEquals(order, result.get());
    }

    @Test
    public void testFindOrderById_NotFound() {
        // given
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // when
        Optional<Order> result = orderService.findById(1);

        // then
        assertFalse(result.isPresent());
    }

    @Test
    public void testAddOrder() {
        // given
        OrderCreateRequest createRequest = createSampleCreateRequest();
        User user = createUser();
        Table table = createTable();
        Order order = createSampleOrder(1);

        when(userRepository.findById(createRequest.getUserId())).thenReturn(Optional.of(user));
        when(tableService.findById(createRequest.getTableId())).thenReturn(Optional.of(table));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // when
        Order result = orderService.addOrder(createRequest);

        // then
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(table, result.getTable());
        assertEquals(Status.OPEN, result.getStatus());
    }


    private List<OrderItem> createSampleOrderItems() {
        // Tworzymy i zwracamy przykładowe OrderItemy
        Order order = createSampleOrder(1);

        OrderItem item1 = OrderItem.builder()
                .order(order)
                .dish(createDish())
                .quantity(2)
                .ready(false)
                .build();

        OrderItem item2 = OrderItem.builder()
                .order(order)
                .dish(createDish())
                .quantity(1)
                .ready(false)
                .build();

        return Arrays.asList(item1, item2);
    }



    @Test
    public void testUpdateOrder_NotFound() {
        // given
        OrderUpdateRequest updateRequest = createSampleUpdateRequest();
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // when
        Optional<Order> result = orderService.updateOrder(1, updateRequest);

        // then
        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateOrder_UserNotFound() {
        // given
        OrderUpdateRequest updateRequest = createSampleUpdateRequest();
        when(orderRepository.findById(1)).thenReturn(Optional.of(createSampleOrder(1)));
        when(userRepository.findById(updateRequest.getUserId())).thenReturn(Optional.empty());

        // when
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1, updateRequest));
    }

    @Test
    public void testUpdateOrder_TableNotFound() {
        // given
        OrderUpdateRequest updateRequest = createSampleUpdateRequest();
        when(orderRepository.findById(1)).thenReturn(Optional.of(createSampleOrder(1)));
        when(userRepository.findById(updateRequest.getUserId())).thenReturn(Optional.of(createUser()));
        when(tableService.findById(updateRequest.getTableId())).thenReturn(Optional.empty());

        // when
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(1, updateRequest));
    }

    @Test
    public void testSetReadyToServe() {
        // given
        Order order = createSampleOrder(1);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // when
        Optional<Order> result = orderService.setReadyToServe(1, true);

        // then
        assertTrue(result.isPresent());
        assertTrue(result.get().getIsReadyToServe());
    }

    @Test
    public void testSetReadyToServe_OrderNotFound() {
        // given
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // when
        Optional<Order> result = orderService.setReadyToServe(1, true);

        // then
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetOpenOrderServeStatus() {
        // given
        List<Order> orders = Arrays.asList(
                createSampleOrder(1),
                createSampleOrder(2)
        );
        when(orderRepository.getOrderByStatusNot(Status.CLOSED)).thenReturn(orders);

        // when
        Optional<List<Map<String, Object>>> result = orderService.getOpenOrderServeStatus();

        // then
        assertTrue(result.isPresent());
        List<Map<String, Object>> resultMap = result.get();
        assertEquals(2, resultMap.size());
        assertEquals(Integer.valueOf(1), resultMap.get(0).get("id"));
        assertEquals(Integer.valueOf(2), resultMap.get(1).get("id"));

    }

    @Test
    public void testDeleteOrder() {
        // given
        when(orderRepository.findById(1)).thenReturn(Optional.of(createSampleOrder(1)));

        // when
        boolean result = orderService.deleteOrder(1);

        // then
        assertTrue(result);
        verify(orderRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteOrder_OrderNotFound() {
        // given
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        // when
        boolean result = orderService.deleteOrder(1);

        // then
        assertFalse(result);
        verify(orderRepository, never()).deleteById(1);
    }

    private Order createSampleOrder(Integer id) {
        Order order = new Order();
        order.setId(id);
        order.setUser(createUser());
        order.setTable(createTable());
        order.setStatus(Status.OPEN);
        return order;
    }

    private OrderCreateRequest createSampleCreateRequest() {
        return OrderCreateRequest.builder()
                .userId(1)
                .tableId(1)
                .build();
    }

    private OrderUpdateRequest createSampleUpdateRequest() {
        return OrderUpdateRequest.builder()
                .userId(1)
                .tableId(1)
                .orderItems(Arrays.asList(createSampleOrderItemRequest()))
                .isStartedProcessing(true)
                .status(Status.IN_PROGRESS)
                .isReadyToServe(true)
                .totalPrice(BigDecimal.valueOf(50.0))
                .build();
    }

    private OrderItemRequest createSampleOrderItemRequest() {
        return OrderItemRequest.builder()
                .dishId(1)
                .quantity(2)
                .ready(true)
                .build();
    }

    private User createUser() {
        User user = new User();
        user.setId(1);
        // set user properties
        return user;
    }

    private Table createTable() {
        Table table = new Table();
        table.setId(1);
        // set table properties
        return table;
    }

    private Dish createDish() {
        Dish dish = new Dish();
        dish.setId(1);
        // set dish properties
        return dish;
    }


}
