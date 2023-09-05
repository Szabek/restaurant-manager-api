package pl.pjatk.RestaurantManager.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.RestaurantManager.dto.UserDto;
import pl.pjatk.RestaurantManager.model.User;
import pl.pjatk.RestaurantManager.request.UserRequest;
import pl.pjatk.RestaurantManager.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin()
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> findAll() {

        List<User> users = userService.findAll();

        return ResponseEntity.ok(users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList())
        );
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Integer id) {
        Optional<User> userById = userService.findById(id);

        return userById.map(this::convertToDto).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/authenticated")
    public ResponseEntity<UserDto> findAuthenticatedUser(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(convertToDto(userService.findByEmail(userEmail).get()));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(convertToDto(userService.addUser(request)));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @PutMapping("/update/{id}")
    public UserDto updateUser(@PathVariable Integer id, @RequestBody UserRequest request) {
        Optional<User> updatedUser = userService.updateUser(id, request);

        if (updatedUser.isPresent()) {
            return convertToDto(updatedUser.get());
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @PreAuthorize("hasAnyAuthority('WAITER')")
    @PutMapping("/update-tables/{id}")
    public UserDto updateUserTables(@PathVariable Integer id, @RequestBody UserRequest request) {
        Optional<User> updatedUser = userService.updateUserTables(id, request);

        if (updatedUser.isPresent()) {
            return convertToDto(updatedUser.get());
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    private UserDto convertToDto(User user) {
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setRole(user.getRole());
        userDto.setTables(user.getTables());
        return userDto;
    }
}
