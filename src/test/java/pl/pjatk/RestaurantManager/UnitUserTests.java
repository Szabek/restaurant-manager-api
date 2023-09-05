package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pjatk.RestaurantManager.model.User;
import pl.pjatk.RestaurantManager.repository.UserRepository;
import pl.pjatk.RestaurantManager.request.UserRequest;
import pl.pjatk.RestaurantManager.service.TableService;
import pl.pjatk.RestaurantManager.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UnitUserTests {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TableService tableService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, tableService, passwordEncoder);
    }

    @Test
    public void shouldFindAllUsers() {
        // given
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        when(userRepository.findAll()).thenReturn(users);

        // when
        List<User> result = userService.findAll();

        // then
        Assertions.assertEquals(users, result);
        Assertions.assertEquals(users.size(), result.size());
    }

    @Test
    public void shouldFindUserById() {
        // given
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // when
        Optional<User> result = userService.findById(1);

        // then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(user, result.get());
        Assertions.assertEquals(user.getId(), result.get().getId());
    }

    @Test
    public void shouldNotFindUserById() {
        // given
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userService.findById(1);

        // then
        Assertions.assertTrue(result.isEmpty());
    }


    @Test
    public void testUpdateUser() {
        // Given
        Integer userId = 1;
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("new-email@example.com");
        userRequest.setFirstname("New Firstname");
        userRequest.setLastname("New Lastname");
        userRequest.setPassword("new-password");
        userRequest.setTableIds(List.of(1, 2, 3));

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old-email@example.com");
        existingUser.setFirstname("Old Firstname");
        existingUser.setLastname("Old Lastname");
        existingUser.setPassword("old-password");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(existingUser);
        Mockito.when(tableService.findAllById(userRequest.getTableIds())).thenReturn(List.of());

        // When
        Optional<User> updatedUser = userService.updateUser(userId, userRequest);

        // Then
        assertTrue(updatedUser.isPresent());
        assertEquals(userId, updatedUser.get().getId());
        assertEquals(userRequest.getEmail(), updatedUser.get().getEmail());
        assertEquals(userRequest.getFirstname(), updatedUser.get().getFirstname());
        assertEquals(userRequest.getLastname(), updatedUser.get().getLastname());
        assertTrue(updatedUser.get().getTables().isEmpty());

        Mockito.verify(userRepository).findById(userId);
        Mockito.verify(userRepository).save(existingUser);
        Mockito.verify(tableService).findAllById(userRequest.getTableIds());
    }


    @Test
    public void shouldDeleteUser() {
        // given
        User existingUser = new User();
        existingUser.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).deleteById(1);

        // when
        boolean result = userService.deleteUser(1);

        // then
        Assertions.assertTrue(result);
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    public void shouldNotDeleteUserWhenNotFound() {
        // given
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // when
        boolean result = userService.deleteUser(1);

        // then
        Assertions.assertFalse(result);
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, never()).deleteById(anyInt());
    }
}
