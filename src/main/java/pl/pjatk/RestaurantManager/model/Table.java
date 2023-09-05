package pl.pjatk.RestaurantManager.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "restaurant_tables")

public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotEmpty(message = "Table must have name")
    private String name;
    private Integer seatsNumber;
    private Boolean isActive;
    private Boolean isOccupied;
    @JsonIgnore
    @ManyToMany(mappedBy = "tables")
    private List<User> users;

    @JsonIgnore
    @OneToMany(mappedBy = "table")
    private List<Order> orders;
}
