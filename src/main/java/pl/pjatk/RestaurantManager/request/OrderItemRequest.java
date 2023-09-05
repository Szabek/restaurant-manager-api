package pl.pjatk.RestaurantManager.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequest {
    private Integer dishId;
    private Integer quantity;
    private Boolean ready;
}
