package pl.pjatk.RestaurantManager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.RestaurantManager.model.Table;
import pl.pjatk.RestaurantManager.request.TableRequest;
import pl.pjatk.RestaurantManager.service.TableService;


import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor
@CrossOrigin()
public class TableController {
    private final TableService tableService;

    @GetMapping("/all")
    public ResponseEntity<List<Table>> findAll() {
        return ResponseEntity.ok(tableService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Table> findById(@PathVariable Integer id) {
        Optional<Table> tableById = tableService.findById(id);

        return tableById.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Table> addTable(@RequestBody @Valid TableRequest request) {
        return ResponseEntity.ok(tableService.addTable(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Optional<Table>> updateTable(@PathVariable Integer id, @RequestBody TableRequest request) {
        return ResponseEntity.ok(tableService.updateTable(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteTable(@PathVariable Integer id) {
        return ResponseEntity.ok(tableService.deleteTable(id));
    }
}
