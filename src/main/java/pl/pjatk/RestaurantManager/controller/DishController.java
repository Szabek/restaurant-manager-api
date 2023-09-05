package pl.pjatk.RestaurantManager.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.RestaurantManager.model.Dish;
import pl.pjatk.RestaurantManager.request.DishRequest;
import pl.pjatk.RestaurantManager.service.DishService;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dish")
@RequiredArgsConstructor
@CrossOrigin()
public class DishController {
    private final DishService dishService;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get("uploads"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Dish>> findAll() {
        return ResponseEntity.ok(dishService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> findById(@PathVariable Integer id) {
        Optional<Dish> dishById = dishService.findById(id);

        return dishById.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Dish> addDish(@ModelAttribute @Valid DishRequest request) {
        return ResponseEntity.ok(dishService.addDish(request));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Dish> updateDish(@PathVariable Integer id, @ModelAttribute @Valid DishRequest request) {
        Optional<Dish> dish = dishService.findById(id);
        if (dish.isPresent()) {
            Dish updatedDish = dishService.updateDish(id, request);
            return ResponseEntity.ok(updatedDish);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteDish(@PathVariable Integer id) {
        return ResponseEntity.ok(dishService.deleteDish(id));
    }
}
