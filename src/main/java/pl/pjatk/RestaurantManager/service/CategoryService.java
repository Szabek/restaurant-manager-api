package pl.pjatk.RestaurantManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.RestaurantManager.model.Category;
import pl.pjatk.RestaurantManager.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Category addCategory(Category newCategory) {
        return categoryRepository.save(newCategory);
    }

    public Optional<Category> updateCategory(Integer id, Category newCategory) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(newCategory.getName());
                    return categoryRepository.save(category);
                });
    }

    public boolean deleteCategory(Integer id) {
        if (categoryRepository.findById(id).isEmpty()) {
            return false;
        }

        categoryRepository.deleteById(id);
        return true;
    }
}
