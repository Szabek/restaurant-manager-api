package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.RestaurantManager.model.Unit;
import pl.pjatk.RestaurantManager.repository.UnitRepository;
import pl.pjatk.RestaurantManager.service.UnitService;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class IntegrationUnitTests {
    @Autowired
    private UnitService unitService;

    @Autowired
    private UnitRepository unitRepository;

    @Test
    public void testFindAll() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");
        unitRepository.save(unit);

        // when
        List<Unit> units = unitService.findAll();

        // then
        Assertions.assertFalse(units.isEmpty());
    }


    @Test
    public void testFindById() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");
        unit = unitRepository.save(unit);

        // when
        Optional<Unit> foundUnit = unitService.findById(unit.getId());

        // then
        Assertions.assertTrue(foundUnit.isPresent());
        Assertions.assertEquals("kg", foundUnit.get().getName());
    }


    @Test
    public void testAddUnit() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");

        // when
        Unit savedUnit = unitService.addUnit(unit);

        // then
        Assertions.assertNotNull(savedUnit.getId());
        Assertions.assertEquals("kg", savedUnit.getName());
    }


    @Test
    public void testUpdateUnit() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");
        unit = unitRepository.save(unit);

        Unit updatedUnit = new Unit();
        updatedUnit.setName("g");

        // when
        Unit savedUnit = unitService.updateUnit(unit.getId(), updatedUnit);

        // then
        Assertions.assertEquals(unit.getId(), savedUnit.getId());
        Assertions.assertEquals("g", savedUnit.getName());
    }


    @Test
    public void testDeleteUnit() {
        // given
        Unit unit = new Unit();
        unit.setName("kg");
        unit = unitRepository.save(unit);

        // when
        boolean result = unitService.deleteUnit(unit.getId());

        // then
        Assertions.assertTrue(result);
        Assertions.assertFalse(unitRepository.existsById(unit.getId()));
    }

}
