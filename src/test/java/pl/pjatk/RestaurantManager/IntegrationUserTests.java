package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.RestaurantManager.model.User;
import pl.pjatk.RestaurantManager.repository.UserRepository;
import pl.pjatk.RestaurantManager.request.UserRequest;
import pl.pjatk.RestaurantManager.service.TableService;
import pl.pjatk.RestaurantManager.service.UserService;

import java.util.List;
import java.util.Optional;

import static pl.pjatk.RestaurantManager.model.Role.ADMIN;

@SpringBootTest
@Transactional
public class IntegrationUserTests {

    @Autowired
    private UserService userService;

    @Autowired
    private TableService tableService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindAll() {
        // given
        User user = createUser();

        // when
        List<User> users = userService.findAll();

        // then
        Assertions.assertFalse(users.isEmpty());
    }

    @Test
    public void testFindById() {
        // given
        User user = createUser();

        // when
        Optional<User> foundUser = userService.findById(user.getId());

        // then
        Assertions.assertTrue(foundUser.isPresent());
        Assertions.assertEquals(user.getEmail(), foundUser.get().getEmail());
    }

    @Test
    public void testAddUser() {
        // given
        UserRequest request = createUserRequest();

        // when
        User savedUser = userService.addUser(request);

        // then
        Assertions.assertNotNull(savedUser.getId());
        Assertions.assertEquals(request.getEmail(), savedUser.getEmail());
    }

    @Test
    public void testUpdateUser() {
        // given
        User user = createUser();

        UserRequest request = UserRequest.builder()
                .email("updatedEmail@test.com")
                .firstname("updatedFirstname")
                .lastname("updatedLastname")
                .password("updatedPassword")
                .role(ADMIN)
                .tableIds(List.of(1))
                .build();

        // when
        Optional<User> updatedUser = userService.updateUser(user.getId(), request);

        // then
        Assertions.assertTrue(updatedUser.isPresent());
        Assertions.assertEquals(request.getEmail(), updatedUser.get().getEmail());
        Assertions.assertEquals(request.getFirstname(), updatedUser.get().getFirstname());
        Assertions.assertEquals(request.getLastname(), updatedUser.get().getLastname());
        Assertions.assertEquals(request.getRole(), updatedUser.get().getRole());
        Assertions.assertEquals(tableService.findAllById(request.getTableIds()), updatedUser.get().getTables());
    }

    @Test
    public void testDeleteUser() {
        // given
        User user = createUser();

        // when
        boolean result = userService.deleteUser(user.getId());

        // then
        Assertions.assertTrue(result);
        Assertions.assertFalse(userRepository.existsById(user.getId()));
    }

    private User createUser() {
        UserRequest request = createUserRequest();
        return userService.addUser(request);
    }

    private UserRequest createUserRequest() {
        return UserRequest.builder()
                .email("test@test.com")
                .firstname("testFirstname")
                .lastname("testLastname")
                .password("testPassword")
                .role(ADMIN)
                .tableIds(List.of(1))
                .build();
    }

}
