package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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

public class UnitUnitClassTests {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitService unitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("FindAll units method")
    void findAll_ShouldReturnAllUnits() {
        // given
        List<Unit> units = new ArrayList<>();
        units.add(new Unit(1, "unit1"));
        units.add(new Unit(2, "unit2"));

        when(unitRepository.findAll()).thenReturn(units);

        // when
        List<Unit> result = unitService.findAll();

        // then
        assertEquals(units, result);
        verify(unitRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("FindById units method")
    void findById_ShouldReturnUnit_WhenIdExists() {
        // given
        Integer id = 1;
        Unit unit = new Unit(id, "unit1");

        when(unitRepository.findById(id)).thenReturn(Optional.of(unit));

        // when
        Optional<Unit> result = unitService.findById(id);

        // then
        assertEquals(Optional.of(unit), result);
        verify(unitRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("FindById units method when Id doesnt exist")
    void findById_ShouldReturnEmptyOptional_WhenIdDoesNotExist() {
        // given
        Integer id = 1;

        when(unitRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Unit> result = unitService.findById(id);

        // then
        assertEquals(Optional.empty(), result);
        verify(unitRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("AddUnit method")
    void addUnit_ShouldAddNewUnit() {
        // given
        Unit unit = new Unit(1, "unit1");

        when(unitRepository.save(any(Unit.class))).thenReturn(unit);

        // when
        Unit result = unitService.addUnit(unit);

        // then
        assertEquals(unit, result);
        verify(unitRepository, times(1)).save(any(Unit.class));
    }

    @Test
    @DisplayName("Update unit when unit exists")
    void updateUnit_ShouldUpdateExistingUnit_WhenIdExists() {
        // given
        Integer id = 1;
        Unit existingUnit = new Unit(id, "unit1");
        Unit updatedUnit = new Unit(id, "updatedUnit1");

        when(unitRepository.findById(id)).thenReturn(Optional.of(existingUnit));
        when(unitRepository.save(existingUnit)).thenReturn(updatedUnit);

        // when
        Unit result = unitService.updateUnit(id, updatedUnit);

        // then
        assertEquals(updatedUnit, result);
        verify(unitRepository, times(1)).findById(id);
        verify(unitRepository, times(1)).save(existingUnit);
    }

    @Test
    @DisplayName("FindAll method with 3 units given")
    public void testFindAll() {
        Unit unit1 = Unit.builder().name("kg").build();
        Unit unit2 = Unit.builder().name("l").build();
        Unit unit3 = Unit.builder().name("piece").build();

        when(unitRepository.findAll()).thenReturn(List.of(unit1, unit2, unit3));

        List<Unit> allUnits = unitService.findAll();

        assertEquals(3, allUnits.size());
        assertTrue(allUnits.contains(unit1));
        assertTrue(allUnits.contains(unit2));
        assertTrue(allUnits.contains(unit3));

        verify(unitRepository).findAll();
    }

    @Test
    @DisplayName("FindByID unit method second test")
    public void testFindById() {
        Unit unit = Unit.builder().id(1).name("kg").build();

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));

        Optional<Unit> foundUnit = unitService.findById(1);

        assertTrue(foundUnit.isPresent());
        assertEquals(unit, foundUnit.get());

        verify(unitRepository).findById(1);
    }

    @Test
    @DisplayName("FindById if Id doesnt exist")
    public void testFindById_NotFound() {
        when(unitRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Unit> foundUnit = unitService.findById(1);

        assertFalse(foundUnit.isPresent());

        verify(unitRepository).findById(1);
    }

    @Test
    @DisplayName("AddUnit method second test")
    public void testAddUnit() {
        Unit newUnit = Unit.builder().name("kg").build();
        Unit savedUnit = Unit.builder().id(1).name("kg").build();

        when(unitRepository.save(newUnit)).thenReturn(savedUnit);

        Unit addedUnit = unitService.addUnit(newUnit);

        assertEquals(savedUnit, addedUnit);

        verify(unitRepository).save(newUnit);
    }

    @Test
    @DisplayName("UpdateUnit method")
    public void testUpdateUnit() {
        Unit unit = Unit.builder().id(1).name("kg").build();
        Unit updatedUnit = Unit.builder().id(1).name("l").build();

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(unitRepository.save(unit)).thenReturn(updatedUnit);

        Unit result = unitService.updateUnit(1, updatedUnit);

        assertEquals(updatedUnit, result);

        verify(unitRepository).findById(1);
        verify(unitRepository).save(unit);
    }


    @Test
    @DisplayName("DeleteUnit method")
    public void testDeleteUnit() {
        Unit unit = Unit.builder().id(1).name("kg").build();

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));

        boolean result = unitService.deleteUnit(1);

        assertTrue(result);

        verify(unitRepository).findById(1);
        verify(unitRepository).deleteById(1);
    }

    @Test
    @DisplayName("DeleteUnit when unit doesnt exist")
    public void testDeleteUnit_NotFound() {
        when(unitRepository.findById(1)).thenReturn(Optional.empty());

        boolean result = unitService.deleteUnit(1);

        assertFalse(result);

        verify(unitRepository).findById(1);
        verify(unitRepository, never()).deleteById(any());
    }
}
