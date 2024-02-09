package com.andresv2.apirest.service;

import com.andresv2.apirest.entities.Product;
import com.andresv2.apirest.repository.ProductRepository;
import com.andresv2.apirest.util.SearchUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private SearchUtils<Product> searchUtilsProduct;

    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    public Product updateProduct(Long id, HashMap<String, Object> userData) {
        Product product = productRepo.findById(id).orElseThrow(() -> {throw new UsernameNotFoundException("User with ID " + id + " not found");});
        userData.forEach((key, value) -> {
            // All names of the keys reference to the keys of the Class
            switch (key) {
                case "name" -> product.setName((String) value);
                case "description" -> product.setDescription((String) value);
                case "price" -> product.setPrice((Double) value);
                case "active" -> product.setActive((Boolean) value);
                case "discount" -> product.setDiscount((Boolean) value);
                case "stock" -> product.setStock((Integer) value);
                case "image" -> product.setImage((String) value);
                case "category" -> product.setCategory((String) value);
                case "genre" -> product.setGenre((String) value);
            }
        });
        return productRepo.save(product);
    }

    public Product findProductById(Long id) {
        return productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    public Page<Product> findAllFilteredProducts(Pageable pageable, JSONObject data) {
        JSONObject filters = new JSONObject();
        JSONObject equalsTo = new JSONObject();
        JSONObject betweenTo = new JSONObject(); //only if filters have between option
        // Define Filters
        // All names of the keys reference to the keys of the Class
        if (data.has("name")) equalsTo.put("name", data.getString("name"));
        if (data.has("description")) equalsTo.put("description", data.getString("description"));
        if (data.has("category")) equalsTo.put("category", data.getString("category"));
        if (data.has("genre")) equalsTo.put("genre", data.getString("genre"));
        if (data.has("discount")) equalsTo.put("discount", data.getBoolean("discount"));
        if (data.has("active")) equalsTo.put("active", data.getBoolean("active"));

        if (data.has("price"))
            if (data.get("price") instanceof JSONArray)
                betweenTo.put("price", data.getJSONArray("price").toList());
            else
                equalsTo.put("price", data.get("price").toString());

        if (data.has("stock"))
            if (data.get("stock") instanceof JSONArray)
                betweenTo.put("stock", data.getJSONArray("stock").toList());
            else
                equalsTo.put("stock", data.get("stock").toString());


        if(equalsTo.length() > 0) filters.put("equals", equalsTo);
        if(betweenTo.length() > 0) filters.put("between", betweenTo); //only if filters have between option

        // if the method have more filters like between or  greater than etc. we need to add the summary of sizes to size key Example size-> filters.put("size", equalsTo.length() + greaterThan.length() + between.length());
        filters.put("size", equalsTo.length() + betweenTo.length()); //

        return productRepo.findAll(searchUtilsProduct.getQueryParameters(filters), pageable);
    }


}
