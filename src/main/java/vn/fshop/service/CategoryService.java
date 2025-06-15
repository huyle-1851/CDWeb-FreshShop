package vn.fshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.fshop.model.Category;
import vn.fshop.repository.CategoryRepository;
import java.util.*;
@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public int getAmountProductById(Integer id) {
        return categoryRepository.countProductsById(id);
    }

    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // Admin functions for category management
    public Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer id, String name) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            throw new RuntimeException("Category not found with id: " + id);
        }

        category.setName(name);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return categoryRepository.findAll().stream()
            .anyMatch(category -> category.getName().equalsIgnoreCase(name));
    }
}