package pl.pjatk.RestaurantManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.pjatk.RestaurantManager.model.Category;
import pl.pjatk.RestaurantManager.repository.CategoryRepository;
import pl.pjatk.RestaurantManager.service.CategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class UnitCategoryTests {
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    @DisplayName("Test findAll method")
    void testFindAll() {
        // given
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(Category.builder().id(1).name("Test1").build());
        categoryList.add(Category.builder().id(2).name("Test2").build());

        when(categoryRepository.findAll()).thenReturn(categoryList);

        // when
        List<Category> result = categoryService.findAll();

        // then
        assertEquals(categoryList, result);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findById method with existing id")
    void testFindById_existingId() {
        // given
        Integer id = 1;
        Category category = Category.builder().id(id).name("Test").build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        // when
        Optional<Category> result = categoryService.findById(id);

        // then
        assertTrue(result.isPresent());
        assertEquals(category, result.get());
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test findById method with non-existent id")
    void testFindById_nonExistentId() {
        // given
        Integer id = 1;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Category> result = categoryService.findById(id);

        // then
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Test addCategory method")
    void testAddCategory() {
        // given
        Category newCategory = Category.builder().id(1).name("Test").build();
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // when
        Category result = categoryService.addCategory(newCategory);

        // then
        assertEquals(newCategory, result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Test updateCategory method with existing id")
    void testUpdateCategory_existingId() {
        // given
        Integer id = 1;
        Category existingCategory = Category.builder().id(id).name("Test").build();
        Category updatedCategory = Category.builder().id(id).name("Updated test").build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);

        // when
        Optional<Category> result = categoryService.updateCategory(id, updatedCategory);

        // then
        assertTrue(result.isPresent());
        assertEquals(updatedCategory, result.get());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(existingCategory);
    }


    @Test
    @DisplayName("Test addCategory method")
    void shouldAddCategory() {
        //given
        Category category = Category.builder()
                .name("New category")
                .build();

        given(categoryRepository.save(category)).willReturn(Category.builder()
                .id(1)
                .name(category.getName())
                .build());

        //when
        Category addedCategory = categoryService.addCategory(category);

        //then
        assertNotNull(addedCategory.getId());
        assertEquals(category.getName(), addedCategory.getName());
        verify(categoryRepository, times(1)).save(category);
    }



    @Test
    @DisplayName("Test updateCategory method")
    void shouldUpdateCategory() {
        //given
        Category category = Category.builder()
                .id(1)
                .name("Updated category")
                .build();

        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));
        given(categoryRepository.save(category)).willReturn(category);

        //when
        Optional<Category> updatedCategory = categoryService.updateCategory(category.getId(), category);

        //then
        assertTrue(updatedCategory.isPresent());
        assertEquals(category.getName(), updatedCategory.get().getName());
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryRepository, times(1)).save(category);
    }


    @Test
    @DisplayName("Test updateCategory method when category doesnt exist")
    void shouldNotUpdateCategoryWhenIdNotFound() {
        //given
        Category category = Category.builder()
                .id(1)
                .name("Updated category")
                .build();

        given(categoryRepository.findById(category.getId())).willReturn(Optional.empty());

        //when
        Optional<Category> updatedCategory = categoryService.updateCategory(category.getId(), category);

        //then
        assertTrue(updatedCategory.isEmpty());
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryRepository, times(0)).save(category);
    }

    @Test
    @DisplayName("Test deleteCategory method")
    void shouldDeleteCategory() {
        //given
        Integer categoryId = 1;
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(Category.builder().id(categoryId).build()));

        //when
        boolean result = categoryService.deleteCategory(categoryId);

        //then
        assertTrue(result);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    @DisplayName("Test deleteCategory method when category doesnt exist")
    void shouldNotDeleteCategoryWhenIdNotFound() {
        //given
        Integer categoryId = 1;
        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        //when
        boolean result = categoryService.deleteCategory(categoryId);

        //then
        assertFalse(result);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(0)).deleteById(categoryId);
    }
}

