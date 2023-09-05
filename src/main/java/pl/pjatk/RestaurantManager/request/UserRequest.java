package pl.pjatk.RestaurantManager.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pjatk.RestaurantManager.model.Role;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private Role role;
    private List<Integer> tableIds;
}
