package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.pjatk.RestaurantManager.model.Table;
import pl.pjatk.RestaurantManager.repository.TableRepository;
import pl.pjatk.RestaurantManager.request.TableRequest;
import pl.pjatk.RestaurantManager.service.TableService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ValidateTableTests {

    private TableService tableService;

    @Mock
    private TableRepository tableRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tableService = new TableService(tableRepository);
    }

    @Test
    @DisplayName("Test findAll method")
    void testFindAll() {
        // given
        List<Table> tableList = new ArrayList<>();
        tableList.add(Table.builder().id(1).name("Table1").build());
        tableList.add(Table.builder().id(2).name("Table2").build());

        when(tableRepository.findAll()).thenReturn(tableList);

        // when
        List<Table> result = tableService.findAll();

        // then
        assertEquals(tableList, result);
        verify(tableRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findAllById method")
    void testFindAllById() {
        // given
        List<Integer> ids = List.of(1, 2);
        List<Table> tableList = new ArrayList<>();
        tableList.add(Table.builder().id(1).name("Table1").build());
        tableList.add(Table.builder().id(2).name("Table2").build());

        when(tableRepository.findAllById(ids)).thenReturn(tableList);

        // when
        List<Table> result = tableService.findAllById(ids);

        // then
        assertEquals(tableList, result);
        verify(tableRepository, times(1)).findAllById(ids);
    }

    @Test
    @DisplayName("Test findById method with existing id")
    void testFindById_existingId() {
        // given
        Integer id = 1;
        Table table = Table.builder().id(id).name("Table").build();

        when(tableRepository.findById(id)).thenReturn(Optional.of(table));

        // when
        Optional<Table> result = tableService.findById(id);

        // then
        assertTrue(result.isPresent());
        assertEquals(table, result.get());
        verify(tableRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test findById method with non-existent id")
    void testFindById_nonExistentId() {
        // given
        Integer id = 1;

        when(tableRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Table> result = tableService.findById(id);

        // then
        assertTrue(result.isEmpty());
        verify(tableRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test addTable method")
    void testAddTable() {
        // given
        TableRequest tableRequest = new TableRequest();
        tableRequest.setName("Table1");
        tableRequest.setSeatsNumber(4);
        tableRequest.setIsActive(true);
        tableRequest.setIsOccupied(false);

        Table table = Table.builder()
                .name(tableRequest.getName())
                .seatsNumber(tableRequest.getSeatsNumber())
                .isActive(tableRequest.getIsActive())
                .isOccupied(tableRequest.getIsOccupied())
                .build();

        when(tableRepository.save(any(Table.class))).thenReturn(table);

        // when
        Table result = tableService.addTable(tableRequest);

        // then
        assertNotNull(result);
        assertEquals(table.getName(), result.getName());
        assertEquals(table.getSeatsNumber(), result.getSeatsNumber());
        assertEquals(table.getIsActive(), result.getIsActive());
        assertEquals(table.getIsOccupied(), result.getIsOccupied());
        verify(tableRepository, times(1)).save(any(Table.class));
    }

    @Test
    @DisplayName("Test updateTable method with existing id")
    void testUpdateTable_existingId() {
        // given
        Integer id = 1;
        TableRequest tableRequest = new TableRequest();
        tableRequest.setName("UpdatedTable");
        tableRequest.setSeatsNumber(4);
        tableRequest.setIsActive(true);
        tableRequest.setIsOccupied(true);

        Table existingTable = Table.builder().id(id).name("Table").build();
        Table updatedTable = Table.builder()
                .id(id)
                .name(tableRequest.getName())
                .seatsNumber(tableRequest.getSeatsNumber())
                .isActive(tableRequest.getIsActive())
                .isOccupied(tableRequest.getIsOccupied())
                .build();

        given(tableRepository.findById(id)).willReturn(Optional.of(existingTable));
        given(tableRepository.save(any(Table.class))).willReturn(updatedTable);

        // when
        Optional<Table> result = tableService.updateTable(id, tableRequest);

        // then
        assertTrue(result.isPresent());
        assertEquals(updatedTable, result.get());
        assertEquals(tableRequest.getName(), result.get().getName());
        assertEquals(tableRequest.getSeatsNumber(), result.get().getSeatsNumber());
        assertEquals(tableRequest.getIsActive(), result.get().getIsActive());
        assertEquals(tableRequest.getIsOccupied(), result.get().getIsOccupied());
        verify(tableRepository, times(1)).findById(id);
        verify(tableRepository, times(1)).save(any(Table.class));
    }

    @Test
    @DisplayName("Test updateTable method with non-existent id")
    void testUpdateTable_nonExistentId() {
        // given
        Integer id = 1;
        TableRequest tableRequest = new TableRequest();
        tableRequest.setName("UpdatedTable");
        tableRequest.setSeatsNumber(4);
        tableRequest.setIsActive(true);
        tableRequest.setIsOccupied(true);

        given(tableRepository.findById(id)).willReturn(Optional.empty());

        // when
        Optional<Table> result = tableService.updateTable(id, tableRequest);

        // then
        assertTrue(result.isEmpty());
        verify(tableRepository, times(1)).findById(id);
        verify(tableRepository, never()).save(any(Table.class));
    }

    @Test
    @DisplayName("Test deleteTable method with existing id")
    void testDeleteTable_existingId() {
        // given
        Integer id = 1;

        when(tableRepository.findById(id)).thenReturn(Optional.of(Table.builder().id(id).build()));

        // when
        boolean result = tableService.deleteTable(id);

        // then
        assertTrue(result);
        verify(tableRepository, times(1)).findById(id);
        verify(tableRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Test deleteTable method with non-existent id")
    void testDeleteTable_nonExistentId() {
        // given
        Integer id = 1;

        when(tableRepository.findById(id)).thenReturn(Optional.empty());

        // when
        boolean result = tableService.deleteTable(id);

        // then
        assertFalse(result);
        verify(tableRepository, times(1)).findById(id);
        verify(tableRepository, never()).deleteById(anyInt());
    }
}
