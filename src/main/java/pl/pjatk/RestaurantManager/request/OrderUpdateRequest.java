package pl.pjatk.RestaurantManager.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pjatk.RestaurantManager.model.Status;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateRequest {
    private Integer userId;
    private Integer tableId;
    private List<OrderItemRequest> orderItems;
    private Boolean isStartedProcessing;
    private Status status;
    private Boolean isReadyToServe;
    private BigDecimal totalPrice;
}
