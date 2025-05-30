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

    public int getAmountProductById(int id) {
        return categoryRepository.countProductsById(id);
    }
}