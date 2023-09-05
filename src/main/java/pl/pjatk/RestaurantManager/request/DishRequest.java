package pl.pjatk.RestaurantManager.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DishRequest {
    private String name;
    private String description;
    private Integer categoryId;
    private BigDecimal price;
    private List<Integer> ingredientIds;
    private List<Double> ingredientQuantities;
    private MultipartFile image;
}
