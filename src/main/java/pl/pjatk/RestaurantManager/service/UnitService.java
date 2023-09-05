package pl.pjatk.RestaurantManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.RestaurantManager.model.Unit;
import pl.pjatk.RestaurantManager.repository.UnitRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepository unitRepository;

    public List<Unit> findAll() {
        return unitRepository.findAll();
    }

    public Optional<Unit> findById(Integer id) {
        return unitRepository.findById(id);
    }

    public Unit addUnit(Unit newUnit) {
        return unitRepository.save(newUnit);
    }

    public Unit updateUnit(Integer id, Unit newUnit) {
        return unitRepository.findById(id)
                .map(unit -> {
                    unit.setName(newUnit.getName());
                    return unitRepository.save(unit);
                })
                .orElseGet(() -> {
                    newUnit.setId(id);
                    return unitRepository.save(newUnit);
                });
    }

    public boolean deleteUnit(Integer id) {
        if (unitRepository.findById(id).isEmpty()) {
            return false;
        }

        unitRepository.deleteById(id);
        return true;
    }
}
