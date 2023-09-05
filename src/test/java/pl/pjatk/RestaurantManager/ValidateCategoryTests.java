package pl.pjatk.RestaurantManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.pjatk.RestaurantManager.model.Category;
import pl.pjatk.RestaurantManager.repository.CategoryRepository;
import pl.pjatk.RestaurantManager.service.CategoryService;

public class ValidateCategoryTests {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        Category category1 = new Category();
        category1.setName("Dish");
        Category category2 = new Category();
        category2.setName("Juice");
        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.findAll();

        assertEquals(categories, result);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        Integer id = 1;
        Category category = new Category();
        category.setName("Juice");
        Optional<Category> optionalCategory = Optional.of(category);

        when(categoryRepository.findById(id)).thenReturn(optionalCategory);

        Optional<Category> result = categoryService.findById(id);

        assertEquals(optionalCategory, result);
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    public void testAddCategory() {
        Category category = new Category();
        category.setName("Juice");

        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.addCategory(category);

        assertEquals(category, result);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void testUpdateCategory() {
        Integer id = 1;
        Category category = new Category();
        category.setName("Juice");
        Category updatedCategory = new Category();
        updatedCategory.setName("Juice1");
        Optional<Category> optionalCategory = Optional.of(category);

        when(categoryRepository.findById(id)).thenReturn(optionalCategory);
        when(categoryRepository.save(category)).thenReturn(updatedCategory);

        Optional<Category> result = categoryService.updateCategory(id, updatedCategory);

        assertTrue(result.isPresent());
        assertEquals(updatedCategory, result.get());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void testUpdateCategoryNotFound() {
        Integer id = 1;
        Category updatedCategory = new Category();
        updatedCategory.setName("New Juice");
        Optional<Category> optionalCategory = Optional.empty();

        when(categoryRepository.findById(id)).thenReturn(optionalCategory);

        Optional<Category> result = categoryService.updateCategory(id, updatedCategory);

        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    public void testDeleteCategory() {
        Integer id = 1;
        Category category = new Category();
        category.setName("Juices");
        Optional<Category> optionalCategory = Optional.of(category);

        when(categoryRepository.findById(id)).thenReturn(optionalCategory);

        boolean result = categoryService.deleteCategory(id);

        assertTrue(result);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).deleteById(id);
    }

    @Test
    public void testDeleteCategoryNotFound() {
        Integer id = 1;
        Optional<Category> optionalCategory = Optional.empty();

        when(categoryRepository.findById(id)).thenReturn(optionalCategory);

        boolean result = categoryService.deleteCategory(id);
        assertFalse(result);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(0)).deleteById(id);
    }
}