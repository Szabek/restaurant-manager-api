package pl.pjatk.RestaurantManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.RestaurantManager.model.Unit;
import pl.pjatk.RestaurantManager.service.UnitService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/unit")
@RequiredArgsConstructor
@CrossOrigin()
public class UnitController {
    private final UnitService unitService;

    @GetMapping("/all")
    public ResponseEntity<List<Unit>> findAll() {
        return ResponseEntity.ok(unitService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unit> findById(@PathVariable Integer id) {
        Optional<Unit> unitById = unitService.findById(id);

        return unitById.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Unit> addUnit(@RequestBody @Valid Unit unit) {
        return ResponseEntity.ok(unitService.addUnit(unit));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Unit> updateUnit(@PathVariable Integer id, @RequestBody Unit unit) {
        return ResponseEntity.ok(unitService.updateUnit(id, unit));
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteUnit(@PathVariable Integer id) {
        return ResponseEntity.ok(unitService.deleteUnit(id));
    }
}
