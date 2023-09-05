package pl.pjatk.RestaurantManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.RestaurantManager.model.Table;
import pl.pjatk.RestaurantManager.repository.TableRepository;
import pl.pjatk.RestaurantManager.request.TableRequest;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;

    public List<Table> findAll() {
        return tableRepository.findAll();
    }

    public List<Table> findAllById(List<Integer> ids) {
        return tableRepository.findAllById(ids);
    }

    public Optional<Table> findById(Integer id) {
        return tableRepository.findById(id);
    }

    public Table addTable(TableRequest request) {
        var table = Table.builder()
                .name(request.getName())
                .seatsNumber(request.getSeatsNumber())
                .isActive(request.getIsActive())
                .isOccupied(request.getIsOccupied())
                .build();
        return tableRepository.save(table);
    }

    public Optional<Table> updateTable(Integer id, TableRequest request) {
        return tableRepository.findById(id)
                .map(table -> {
                    table.setName(request.getName());
                    table.setSeatsNumber(request.getSeatsNumber());
                    table.setIsActive(request.getIsActive());
                    table.setIsOccupied(request.getIsOccupied());
                    return tableRepository.save(table);
                });
    }

    public Optional<Table> updateIsOccupied(Integer id, Boolean isOccupied) {
        return tableRepository.findById(id)
                .map(table -> {
                    table.setName(table.getName());
                    table.setSeatsNumber(table.getSeatsNumber());
                    table.setIsActive(table.getIsActive());
                    table.setIsOccupied(isOccupied);
                    return tableRepository.save(table);
                });
    }

    public boolean deleteTable(Integer id) {
        if (tableRepository.findById(id).isEmpty()) {
            return false;
        }

        tableRepository.deleteById(id);
        return true;
    }
}
