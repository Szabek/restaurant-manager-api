package pl.pjatk.RestaurantManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.pjatk.RestaurantManager.model.Table;
import pl.pjatk.RestaurantManager.repository.TableRepository;
import pl.pjatk.RestaurantManager.request.TableRequest;
import pl.pjatk.RestaurantManager.service.TableService;

@ExtendWith(MockitoExtension.class)
public class UnitTableTests {

    @Mock
    private TableRepository tableRepository;

    @InjectMocks
    private TableService tableService;

    private Table table;
    private List<Table> tables;

    @BeforeEach
    public void setUp() {
        table = new Table();
        table.setId(1);
        table.setName("New Table");
        table.setSeatsNumber(8);
        table.setIsActive(true);
        table.setIsOccupied(false);

        tables = new ArrayList<>();
        tables.add(table);
    }

    @Test
    @DisplayName("Test findAll method")
    public void testFindAll() {
        when(tableRepository.findAll()).thenReturn(tables);

        List<Table> result = tableService.findAll();

        assertEquals(tables, result);
    }
    @Test
    @DisplayName("Test findAll method when no tables exist")
    void findAllEmptyTableTest() {
        when(tableRepository.findAll()).thenReturn(Collections.emptyList());
        List<Table> tables = tableService.findAll();
        assertTrue(tables.isEmpty());
    }

    @Test
    @DisplayName("Test findById method with existing id")
    public void testFindById() {
        when(tableRepository.findById(1)).thenReturn(Optional.of(table));

        Optional<Table> result = tableService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(table, result.get());
    }

    @Test
    @DisplayName("Test findById method with non-existent id")
    public void testFindByIdNotFound() {
        when(tableRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Table> result = tableService.findById(1);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test addTable method")
    public void testAddTable() {
        TableRequest tableRequest = new TableRequest();
        tableRequest.setName("Big Table");
        tableRequest.setSeatsNumber(11);
        tableRequest.setIsActive(true);
        tableRequest.setIsOccupied(false);

        Table addedTable = new Table();
        addedTable.setId(3);
        addedTable.setName(tableRequest.getName());
        addedTable.setSeatsNumber(tableRequest.getSeatsNumber());
        addedTable.setIsActive(tableRequest.getIsActive());
        addedTable.setIsOccupied(tableRequest.getIsOccupied());

        when(tableRepository.save(any(Table.class))).thenReturn(addedTable);

        Table result = tableService.addTable(tableRequest);

        assertEquals(addedTable, result);
    }

    @Test
    @DisplayName("Test updateTable method")
    public void testUpdateTable() {
        TableRequest tableRequest = new TableRequest();
        tableRequest.setName("Table 1");
        tableRequest.setSeatsNumber(6);
        tableRequest.setIsActive(true);
        tableRequest.setIsOccupied(false);

        Table updatedTable = new Table();
        updatedTable.setId(1);
        updatedTable.setName(tableRequest.getName());
        updatedTable.setSeatsNumber(tableRequest.getSeatsNumber());
        updatedTable.setIsActive(tableRequest.getIsActive());
        updatedTable.setIsOccupied(tableRequest.getIsOccupied());

        when(tableRepository.findById(1)).thenReturn(Optional.of(table));
        when(tableRepository.save(any(Table.class))).thenReturn(updatedTable);

        Optional<Table> result = tableService.updateTable(1, tableRequest);

        assertTrue(result.isPresent());
        assertEquals(updatedTable, result.get());
    }

    @Test
    @DisplayName("Test deleteTable method")
    public void testDeleteTable() {
        when(tableRepository.findById(1)).thenReturn(Optional.of(table));

        boolean result = tableService.deleteTable(1);

        assertTrue(result);
        verify(tableRepository, times(1)).deleteById(1);
    }
    @Test
    @DisplayName("Test deleteTable method when table doesnt exist")
    void testDeleteTableNonexistent() {
        // given
        Integer tableId = 1;
        when(tableRepository.findById(tableId)).thenReturn(Optional.empty());

        // when
        boolean result = tableService.deleteTable(tableId);

        // then
        assertFalse(result);
        verify(tableRepository).findById(tableId);
        verify(tableRepository, never()).deleteById(tableId);
    }
    @Test
    @DisplayName("Test findById when Table doesnt exist")
    void findByIdNonexistentTableTest() {
        when(tableRepository.findById(1)).thenReturn(Optional.empty());
        Optional<Table> table = tableService.findById(1);
        assertTrue(table.isEmpty());
    }
    @Test
    @DisplayName("Test deleteTable when Table doesnt exist")
    void deleteNonexistentTableTest() {
        when(tableRepository.findById(1)).thenReturn(Optional.empty());
        boolean result = tableService.deleteTable(1);
        assertFalse(result);
        verify(tableRepository, never()).deleteById(anyInt());
    }
    @Test
    @DisplayName("Test updateTable when Table doesnt exist")
    public void testUpdateTableNotFound() {
        TableRequest tableRequest = new TableRequest();
        tableRequest.setName("Table 1");
        tableRequest.setSeatsNumber(6);
        tableRequest.setIsActive(true);
        tableRequest.setIsOccupied(false);

        when(tableRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Table> result = tableService.updateTable(1, tableRequest);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Test validate data when updateTable ")
    public void testUpdateTableValidation() {
        TableRequest tableRequest = new TableRequest();
        tableRequest.setName("Table 1");
        tableRequest.setSeatsNumber(0);
        tableRequest.setIsActive(true);
        tableRequest.setIsOccupied(false);

        when(tableRepository.findById(1)).thenReturn(Optional.of(table));

        Optional<Table> result = tableService.updateTable(1, tableRequest);

        assertFalse(result.isPresent());
    }


}