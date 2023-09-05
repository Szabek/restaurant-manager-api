package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.pjatk.RestaurantManager.model.Unit;
import pl.pjatk.RestaurantManager.repository.UnitRepository;
import pl.pjatk.RestaurantManager.service.UnitService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ValidateUnitTests {
    private UnitService unitService;

    @Mock
    private UnitRepository unitRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        unitService = new UnitService(unitRepository);
    }

    @Test
    @DisplayName("Test findAll")
    void testFindAll() {
        // given
        List<Unit> units = new ArrayList<>();
        units.add(new Unit(1, "Unit 1"));
        units.add(new Unit(2, "Unit 2"));

        when(unitRepository.findAll()).thenReturn(units);

        // when
        List<Unit> result = unitService.findAll();

        // then
        assertEquals(units, result);
        verify(unitRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findById with existing unit")
    void testFindById_existingUnit() {
        // given
        Integer unitId = 1;
        Unit unit = new Unit(unitId, "Unit 1");

        when(unitRepository.findById(unitId)).thenReturn(Optional.of(unit));

        // when
        Optional<Unit> result = unitService.findById(unitId);

        // then
        assertTrue(result.isPresent());
        assertEquals(unit, result.get());
        verify(unitRepository, times(1)).findById(unitId);
    }

    @Test
    @DisplayName("Test findById with non-existing unit")
    void testFindById_nonExistingUnit() {
        // given
        Integer unitId = 1;

        when(unitRepository.findById(unitId)).thenReturn(Optional.empty());

        // when
        Optional<Unit> result = unitService.findById(unitId);

        // then
        assertFalse(result.isPresent());
        verify(unitRepository, times(1)).findById(unitId);
    }

    @Test
    @DisplayName("Test addUnit")
    void testAddUnit() {
        // given
        Unit newUnit = new Unit(1, "New Unit");

        when(unitRepository.save(any(Unit.class))).thenReturn(newUnit);

        // when
        Unit result = unitService.addUnit(newUnit);

        // then
        assertNotNull(result);
        assertEquals(newUnit, result);
        verify(unitRepository, times(1)).save(any(Unit.class));
    }

    @Test
    @DisplayName("Test updateUnit with existing unit")
    void testUpdateUnit_existingUnit() {
        // given
        Integer unitId = 1;
        Unit existingUnit = new Unit(unitId, "Unit 1");
        Unit updatedUnit = new Unit(unitId, "Updated Unit");

        when(unitRepository.findById(unitId)).thenReturn(Optional.of(existingUnit));
        when(unitRepository.save(any(Unit.class))).thenReturn(updatedUnit);

        // when
        Unit result = unitService.updateUnit(unitId, updatedUnit);

        // then
        assertNotNull(result);
        assertEquals(updatedUnit, result);
        verify(unitRepository, times(1)).findById(unitId);
        verify(unitRepository, times(1)).save(any(Unit.class));
    }

    @Test
    @DisplayName("Test updateUnit with non-existing unit")
    void testUpdateUnit_nonExistingUnit() {
        // given
        Integer unitId = 1;
        Unit newUnit = new Unit(unitId, "New Unit");

        when(unitRepository.findById(unitId)).thenReturn(Optional.empty());
        when(unitRepository.save(any(Unit.class))).thenReturn(newUnit);

        // when
        Unit result = unitService.updateUnit(unitId, newUnit);

        // then
        assertNotNull(result);
        assertEquals(newUnit, result);
        verify(unitRepository, times(1)).findById(unitId);
        verify(unitRepository, times(1)).save(any(Unit.class));
    }

    @Test
    @DisplayName("Test deleteUnit with existing unit")
    void testDeleteUnit_existingUnit() {
        // given
        Integer unitId = 1;

        when(unitRepository.findById(unitId)).thenReturn(Optional.of(new Unit(unitId, "Unit 1")));

        // when
        boolean result = unitService.deleteUnit(unitId);

        // then
        assertTrue(result);
        verify(unitRepository, times(1)).findById(unitId);
        verify(unitRepository, times(1)).deleteById(unitId);
    }

    @Test
    @DisplayName("Test deleteUnit with non-existing unit")
    void testDeleteUnit_nonExistingUnit() {
        // given
        Integer unitId = 1;

        when(unitRepository.findById(unitId)).thenReturn(Optional.empty());

        // when
        boolean result = unitService.deleteUnit(unitId);

        // then
        assertFalse(result);
        verify(unitRepository, times(1)).findById(unitId);
        verify(unitRepository, never()).deleteById(unitId);
    }
}
