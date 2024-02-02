package com.andresv2.apirest.service;

import com.andresv2.apirest.entities.Product;
import com.andresv2.apirest.entities.Store;
import com.andresv2.apirest.repository.ProductRepository;
import com.andresv2.apirest.util.SearchUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private SearchUtils<Product> searchUtilsProduct;

    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    public Product findProductById(Long id) {
        return productRepo.findById(id).get();
    }

    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepo.findAll(pageable);
    }

    public Page<Product> findAllFilteredProducts(Pageable pageable, JSONObject data) {
        JSONObject filters = new JSONObject();
        JSONObject equalsTo = new JSONObject();
//        JSONObject between = new JSONObject(); //only if filters have between option
        // Define Filters
        if (data.has("name")) equalsTo.put("name", data.getString("name"));
        if (data.has("description")) equalsTo.put("description", data.getString("description"));
        if (data.has("ramo")) equalsTo.put("ramo", data.getString("ramo"));
        if (data.has("category")) equalsTo.put("category", data.getString("category"));

        if(equalsTo.length() > 0) filters.put("equals", equalsTo);
//        if(between.length() > 0) filters.put("between", between); //only if filters have between option

        // if the method have more filters like between or  greater than etc. we need to add the summary of sizes to size key Example size-> filters.put("size", equalsTo.length() + greaterThan.length() + between.length());
        filters.put("size", equalsTo.length()); // + between.length()

        return productRepo.findAll(searchUtilsProduct.getQueryParameters(filters), pageable);
    }


}
