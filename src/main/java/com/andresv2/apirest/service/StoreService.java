package com.andresv2.apirest.service;

import com.andresv2.apirest.entities.Store;
import com.andresv2.apirest.repository.StoreRepository;
import com.andresv2.apirest.util.SearchUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepo;
    @Autowired
    private SearchUtils<Store> searchUtilsStore;
    @Autowired
    private CategoryService categoryService;

    public Store saveStore(Store store) {
        return storeRepo.save(store);
    }

    public Store findStoreById(Long id) {
        // Use A separated consult 'cause we need only in this case search the categories of a store but on a list
        Store store = storeRepo.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));

        store.setCategories(categoryService.getCategoriesByStoreId(id));
        return store;
    }

    public Page<Store> findAllStores(Pageable pageable) {
        return storeRepo.findAll(pageable);
    }

    public Page<Store> findAllFilteredStores(Pageable pageable, JSONObject data) {
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

        return storeRepo.findAll(searchUtilsStore.getQueryParameters(filters), pageable);
    }
}
