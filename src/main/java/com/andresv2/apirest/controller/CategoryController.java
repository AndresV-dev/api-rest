package com.andresv2.apirest.controller;

import com.andresv2.apirest.entities.Category;
import com.andresv2.apirest.repository.ProductRepository;
import com.andresv2.apirest.service.CategoryService;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("v1/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductRepository productRepo;

    @PostMapping("register")
    public ResponseEntity<Category> insertCategory(@Valid @RequestBody Category category){
        return ResponseEntity.ok().body(categoryService.saveCategory(category));
    }

    @PostMapping("update")
    public ResponseEntity<Category> updateCategory(@Valid @RequestBody Category category){
        return ResponseEntity.ok().body(categoryService.saveCategory(category));
    }

    @GetMapping("id/{id}")
    public ResponseEntity<Category> findCategoryById(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(categoryService.findCategoryById(id));
    }

    @GetMapping("list")
    public ResponseEntity<List<Category>> findCategoriesList(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size){
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        return ResponseEntity.ok().body(categoryService.findAllCategories(pageable).getContent());
    }

    @GetMapping("list/filtered")
    public ResponseEntity<List<Category>> findCategoriesListFiltered(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestBody HashMap<String, Object> data){
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        return ResponseEntity.ok().body(categoryService.findAllFilteredCategories(pageable, new JSONObject(data)).getContent());
    }

    @PostMapping("{categoryId}/add/products")
    public ResponseEntity<JSONObject> addProductToCategory(@PathVariable("categoryId") Long categoryId, @RequestBody HashMap<String, Object> data){
        return ResponseEntity.ok().body(categoryService.addProductToCategory(categoryId, new JSONObject(data)));
    }
}
