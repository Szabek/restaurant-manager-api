package pl.pjatk.RestaurantManager.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableRequest {
    private String name;
    private Integer seatsNumber;
    private Boolean isActive;
    private Boolean isOccupied;
}
