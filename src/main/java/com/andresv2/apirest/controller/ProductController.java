package com.andresv2.apirest.controller;

import com.andresv2.apirest.entities.Product;
import com.andresv2.apirest.entities.Store;
import com.andresv2.apirest.service.ProductService;
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
@RequestMapping("v1/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("register")
    public ResponseEntity<Product> insert(@Valid @RequestBody Product product){
        return ResponseEntity.ok().body(productService.saveProduct(product));
    }

    @PostMapping("update")
    public ResponseEntity<Product> update(@Valid @RequestBody Product product){
        return ResponseEntity.ok().body(productService.saveProduct(product));
    }

    @GetMapping("id/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(productService.findProductById(id));
    }

    @GetMapping("list")
    public ResponseEntity<List<Product>> findProduct(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size){
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        return ResponseEntity.ok().body(productService.findAllProducts(pageable).getContent());
    }

    @PostMapping("list/filtered")
    public ResponseEntity<List<Product>> findProducts(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestBody HashMap<String, Object> data){
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        return ResponseEntity.ok().body(productService.findAllFilteredProducts(pageable, new JSONObject(data)).getContent());
    }


}
