package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pjatk.RestaurantManager.model.Role;
import pl.pjatk.RestaurantManager.model.User;
import pl.pjatk.RestaurantManager.repository.UserRepository;
import pl.pjatk.RestaurantManager.request.UserRequest;
import pl.pjatk.RestaurantManager.service.TableService;
import pl.pjatk.RestaurantManager.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ValidateUserTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TableService tableService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, tableService, passwordEncoder);
    }

    @Test
    void testAddUser_ValidRequest_ShouldReturnUser() {
        // Given
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setPassword("password");
        request.setRole(Role.COOK);
        request.setTableIds(List.of(1, 2));

        when(tableService.findAllById(request.getTableIds())).thenReturn(List.of());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1); // Set a mock ID for the saved user
            return savedUser;
        });

        // When
        User result = userService.addUser(request);

        // Then
        assertNotNull(result);
        assertEquals(request.getEmail(), result.getEmail());
        assertEquals(request.getFirstname(), result.getFirstname());
        assertEquals(request.getLastname(), result.getLastname());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(request.getRole(), result.getRole());
        assertEquals(List.of(), result.getTables());
        assertNotNull(result.getId());
    }

    @Test
    void testUpdateUser_ValidRequest_ShouldReturnUpdatedUser() {
        // Given
        Integer id = 1;
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setPassword("newPassword");
        request.setRole(Role.COOK);
        request.setTableIds(List.of(1, 2));

        User existingUser = new User();
        existingUser.setId(id);
        existingUser.setEmail("old@example.com");
        existingUser.setFirstname("Old");
        existingUser.setLastname("User");
        existingUser.setPassword("oldPassword");
        existingUser.setRole(Role.COOK);

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(tableService.findAllById(request.getTableIds())).thenReturn(List.of());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // When
        Optional<User> result = userService.updateUser(id, request);

        // Then
        assertTrue(result.isPresent());
        User updatedUser = result.get();
        assertEquals(id, updatedUser.getId());
        assertEquals(request.getEmail(), updatedUser.getEmail());
        assertEquals(request.getFirstname(), updatedUser.getFirstname());
        assertEquals(request.getLastname(), updatedUser.getLastname());
        assertEquals("encodedNewPassword", updatedUser.getPassword());
        assertEquals(request.getRole(), updatedUser.getRole());
        assertEquals(List.of(), updatedUser.getTables());
    }

    @Test
    void testUpdateUser_NonExistingId_ShouldReturnEmptyOptional() {
        // Given
        Integer id = 1;
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setFirstname("John");
        request.setLastname("Doe");
        request.setPassword("newPassword");
        request.setRole(Role.ADMIN);
        request.setTableIds(List.of(1, 2));

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.updateUser(id, request);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteUser_ExistingId_ShouldReturnTrue() {
        // Given
        Integer id = 1;
        when(userRepository.findById(id)).thenReturn(Optional.of(new User()));

        // When
        boolean result = userService.deleteUser(id);

        // Then
        assertTrue(result);
    }

    @Test
    void testDeleteUser_NonExistingId_ShouldReturnFalse() {
        // Given
        Integer id = 1;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // When
        boolean result = userService.deleteUser(id);

        // Then
        assertFalse(result);
    }
}
