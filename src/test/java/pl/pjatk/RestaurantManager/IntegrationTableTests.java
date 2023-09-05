package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.RestaurantManager.model.Table;
import pl.pjatk.RestaurantManager.repository.TableRepository;
import pl.pjatk.RestaurantManager.request.TableRequest;
import pl.pjatk.RestaurantManager.service.TableService;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class IntegrationTableTests {
    @Autowired
    private TableService tableService;

    @Autowired
    private TableRepository tableRepository;


    @Test
    public void testFindAll() {
        //given
        Table table = new Table();
        table.setName("Table 1");
        table.setSeatsNumber(4);
        table.setIsActive(true);
        table.setIsOccupied(false);
        tableRepository.save(table);

        //when
        List<Table> tables = tableService.findAll();

        //then
        Assertions.assertFalse(tables.isEmpty());
    }

    @Test
    public void testFindById() {
        Table table = new Table();
        table.setName("Table 1");
        table.setSeatsNumber(4);
        table.setIsActive(true);
        table.setIsOccupied(false);
        table = tableRepository.save(table);

        Optional<Table> foundTable = tableService.findById(table.getId());
        Assertions.assertTrue(foundTable.isPresent());
        Assertions.assertEquals("Table 1", foundTable.get().getName());
        Assertions.assertEquals(4, foundTable.get().getSeatsNumber());
        Assertions.assertEquals(true, foundTable.get().getIsActive());
        Assertions.assertEquals(false, foundTable.get().getIsOccupied());
    }

    @Test
    public void testAddTable() {
        // given
        TableRequest request = new TableRequest();
        request.setName("Table 1");
        request.setSeatsNumber(4);
        request.setIsActive(true);
        request.setIsOccupied(false);

        // when
        Table savedTable = tableService.addTable(request);

        // then
        Assertions.assertNotNull(savedTable.getId());
        Assertions.assertEquals("Table 1", savedTable.getName());
        Assertions.assertEquals(4, savedTable.getSeatsNumber());
        Assertions.assertTrue(savedTable.getIsActive());
        Assertions.assertFalse(savedTable.getIsOccupied());
    }



    @Test
    public void testUpdateTable() {
        // given
        Table table = new Table();
        table.setName("Table 1");
        table.setSeatsNumber(4);
        table.setIsActive(true);
        table.setIsOccupied(false);
        table = tableRepository.save(table);

        TableRequest updatedTable = TableRequest.builder()
                .name("Table 2")
                .seatsNumber(6)
                .isActive(false)
                .isOccupied(true)
                .build();

        // when
        Optional<Table> savedTable = tableService.updateTable(table.getId(), updatedTable);

        // then
        Assertions.assertTrue(savedTable.isPresent());
        Assertions.assertEquals(table.getId(), savedTable.get().getId());
        Assertions.assertEquals("Table 2", savedTable.get().getName());
        Assertions.assertEquals(6, savedTable.get().getSeatsNumber());
        Assertions.assertEquals(false, savedTable.get().getIsActive());
        Assertions.assertEquals(true, savedTable.get().getIsOccupied());
    }



    @Test
    public void testDeleteTable() {
        // given
        Table table = new Table();
        table.setName("Table 1");
        table.setSeatsNumber(4);
        table.setIsActive(true);
        table.setIsOccupied(false);
        table = tableRepository.save(table);

        // when
        boolean result = tableService.deleteTable(table.getId());

        // then
        Assertions.assertTrue(result);
        Assertions.assertFalse(tableRepository.existsById(table.getId()));
    }

}
