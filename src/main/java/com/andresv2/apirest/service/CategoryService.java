package com.andresv2.apirest.service;

import com.andresv2.apirest.entities.Category;
import com.andresv2.apirest.repository.CategoryRepository;
import com.andresv2.apirest.repository.ProductRepository;
import com.andresv2.apirest.util.SearchUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Category findCategoryById(Long id) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));

        category.setProducts(productRepo.findAllByCategory(category.getName()));
        return category;
    }

    public Page<Category> findAllCategories(Pageable pageable) {
        return categoryRepo.findAll(pageable);
    }

    public Page<Category> findAllFilteredCategories(Pageable pageable, JSONObject data) {
        JSONObject filters = new JSONObject();
        JSONObject equalsTo = new JSONObject();
//        JSONObject between = new JSONObject(); //only if filters have between option
        // Define Filters
        if (data.has("name")) equalsTo.put("name", data.getString("name"));
        if (data.has("description")) equalsTo.put("description", data.getString("description"));

        if(equalsTo.length() > 0) filters.put("equals", equalsTo);
//        if(between.length() > 0) filters.put("between", between); //only if filters have between option

        // if the method have more filters like between or  greater than etc. we need to add the summary of sizes to size key Example size-> filters.put("size", equalsTo.length() + greaterThan.length() + between.length());
        filters.put("size", equalsTo.length()); // + between.length()

        return categoryRepo.findAll(searchUtilsCategory.getQueryParameters(filters), pageable);
    }

    public List<Category> getCategoriesByStoreId(Long storeId) {
        return categoryRepo.getCategoriesByStoreId(storeId); //return the categories of the store by storeId
    }
}
