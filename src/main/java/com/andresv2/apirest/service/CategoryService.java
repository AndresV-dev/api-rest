package com.andresv2.apirest.service;

import com.andresv2.apirest.entities.Category;
import com.andresv2.apirest.repository.CategoryRepository;
import com.andresv2.apirest.repository.ProductRepository;
import com.andresv2.apirest.util.SearchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private ProductRepository  productRepo;
    @Autowired
    private SearchUtils<Category> searchUtilsCategory;

    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }

    public Category updateCategory(Long id, HashMap<String, Object> userData) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> {throw new UsernameNotFoundException("User with ID " + id + " not found");});
        userData.forEach((key, value) -> {
            switch (key) {
                case "name" -> category.setName((String) value);
                case "description" -> category.setDescription((String) value);
            }
        });
        return categoryRepo.save(category);
    }

    public Category findCategoryById(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));

        category.setProducts(productRepo.findAllByCategory(category.getName()));
        return category;
    }

    public Page<Category> findAllCategories(Pageable pageable) {
        return categoryRepo.findAll(pageable);
    }

    public List<Category> getCategoriesByStoreId(Long storeId) {
        return categoryRepo.getCategoriesByStoreId(storeId); //return the categories of the store by storeId
    }
}
