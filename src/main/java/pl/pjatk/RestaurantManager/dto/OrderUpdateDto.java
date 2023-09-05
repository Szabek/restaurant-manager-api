package pl.pjatk.RestaurantManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pjatk.RestaurantManager.model.OrderItem;
import pl.pjatk.RestaurantManager.model.Status;
import pl.pjatk.RestaurantManager.model.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateDto {

    private Integer id;
    private Integer userId;
    private String userName;
    private Table table;
    private List<OrderItem> orderItems;
    private Timestamp startDateTime;
    private Timestamp orderProcessingStart;
    private Status status;
    private Boolean isReadyToServe;
    private BigDecimal totalPrice;
    private Duration duration;
}
