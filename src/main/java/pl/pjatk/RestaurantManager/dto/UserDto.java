package pl.pjatk.RestaurantManager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pjatk.RestaurantManager.model.Role;
import pl.pjatk.RestaurantManager.model.Table;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String email;
    private String firstname;
    private String lastname;
    private Role role;
    private List<Table> tables;
}
