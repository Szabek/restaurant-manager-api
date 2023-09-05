package pl.pjatk.RestaurantManager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "dishes")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Dish name needed")
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn()
    private Category category;

    private String image;

    private BigDecimal price;

    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL)
    private List<DishIngredient> dishIngredients;

    @JsonIgnore
    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}