package pl.pjatk.RestaurantManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.RestaurantManager.model.Ingredient;
import pl.pjatk.RestaurantManager.request.IngredientRequest;
import pl.pjatk.RestaurantManager.service.IngredientService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingredient")
@RequiredArgsConstructor
@CrossOrigin()
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping("/all")
    public ResponseEntity<List<Ingredient>> findAll() {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> findById(@PathVariable Integer id) {
        Optional<Ingredient> ingredientById = ingredientService.findById(id);

        return ingredientById.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Ingredient> addIngredient(@RequestBody @Valid IngredientRequest request) {
        return ResponseEntity.ok(ingredientService.addIngredient(request));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Optional<Ingredient>> updateIngredient(
            @PathVariable Integer id,
            @RequestBody IngredientRequest request
    ) {
        return ResponseEntity.ok(ingredientService.updateIngredient(id, request));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteIngredient(@PathVariable Integer id) {
        return ResponseEntity.ok(ingredientService.deleteIngredient(id));
    }
}
