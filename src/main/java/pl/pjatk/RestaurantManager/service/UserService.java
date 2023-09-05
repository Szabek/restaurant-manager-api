package pl.pjatk.RestaurantManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pjatk.RestaurantManager.model.User;
import pl.pjatk.RestaurantManager.repository.UserRepository;
import pl.pjatk.RestaurantManager.request.UserRequest;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TableService tableService;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User addUser(UserRequest request) {
        var user = User.builder()
                .email((request.getEmail()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .tables(tableService.findAllById(request.getTableIds()))
                .build();
        return userRepository.save(user);
    }

    public Optional<User> updateUser(Integer id, UserRequest request) {
        return userRepository.findById(id).map(user -> {
            user.setEmail(request.getEmail());
            user.setFirstname(request.getFirstname());
            user.setLastname(request.getLastname());
            user.setPassword(
                    request.getPassword().isEmpty() ?
                            user.getPassword() :
                            passwordEncoder.encode(request.getPassword()));
            user.setTables(tableService.findAllById(request.getTableIds()));
            return userRepository.save(user);
        });
    }

    public Optional<User> updateUserTables(Integer userId, UserRequest request) {
        return userRepository.findById(userId).map(user -> {
            user.setTables(tableService.findAllById(request.getTableIds()));

            return userRepository.save(user);
        });
    }

    public boolean deleteUser(Integer id) {
        if (userRepository.findById(id).isEmpty()) {
            return false;
        }

        userRepository.deleteById(id);
        return true;
    }
}
