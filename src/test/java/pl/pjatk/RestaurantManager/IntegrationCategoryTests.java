package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.pjatk.RestaurantManager.model.Category;
import pl.pjatk.RestaurantManager.repository.CategoryRepository;
import pl.pjatk.RestaurantManager.service.CategoryService;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class IntegrationCategoryTests {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testFindAll() {
        // given
        Category category = new Category();
        category.setName("Drinks");
        categoryRepository.save(category);

        // when
        List<Category> categories = categoryService.findAll();

        // then
        Assertions.assertFalse(categories.isEmpty());
    }

    @Test
    public void testFindById() {
        // given
        Category category = new Category();
        category.setName("Drinks");
        category = categoryRepository.save(category);

        // when
        Optional<Category> foundCategory = categoryService.findById(category.getId());

        // then
        Assertions.assertTrue(foundCategory.isPresent());
        Assertions.assertEquals("Drinks", foundCategory.get().getName());
    }

    @Test
    public void testAddCategory() {
        // given
        Category category = new Category();
        category.setName("Drinks");

        // when
        Category savedCategory = categoryService.addCategory(category);

        // then
        Assertions.assertNotNull(savedCategory.getId());
        Assertions.assertEquals("Drinks", savedCategory.getName());
    }

    @Test
    public void testUpdateCategory() {
        // given
        Category category = new Category();
        category.setName("Drinks");
        category = categoryRepository.save(category);

        Category updatedCategory = new Category();
        updatedCategory.setName("Desserts");

        // when
        Optional<Category> savedCategory = categoryService.updateCategory(category.getId(), updatedCategory);

        // then
        Assertions.assertTrue(savedCategory.isPresent());
        Assertions.assertEquals(category.getId(), savedCategory.get().getId());
        Assertions.assertEquals("Desserts", savedCategory.get().getName());
    }

    @Test
    public void testDeleteCategory() {
        // given
        Category category = new Category();
        category.setName("Drinks");
        category = categoryRepository.save(category);

        // when
        boolean result = categoryService.deleteCategory(category.getId());

        // then
        Assertions.assertTrue(result);
        Assertions.assertFalse(categoryRepository.existsById(category.getId()));
    }
}
