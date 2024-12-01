package com.andresv2.apirest.controller;

import com.andresv2.apirest.entities.Store;
import com.andresv2.apirest.service.StoreService;
import com.andresv2.apirest.util.RecursiveMethodsUtils;
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
@RequestMapping(value = "v1/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping("register")
    public ResponseEntity<Store> insert(@Valid @RequestBody Store store){
        return ResponseEntity.ok().body(storeService.saveStore(store));
    }

    @PostMapping("update")
    public ResponseEntity<Store> update(@Valid @RequestBody Store store){
        return ResponseEntity.ok().body(storeService.saveStore(store));
    }

    @GetMapping("id/{id}")
    public ResponseEntity<Store> findStoreById(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(storeService.findStoreById(id));
    }

    @GetMapping("list")
    public ResponseEntity<List<Store>> findStores(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,@RequestParam(value = "sortBy", required = false) String sortBy, @RequestBody HashMap<String, Object> filterData){
        Pageable pageable = RecursiveMethodsUtils.getPageable(filterData, page, size, sortBy);
        return ResponseEntity.ok().body(storeService.findAllStores(pageable).getContent());
    }

    @PostMapping("list/filtered")
    public ResponseEntity<List<Store>> findStoresFiltered(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,@RequestParam(value = "sortBy", required = false) String sortBy, @RequestBody HashMap<String, Object> data){
        Pageable pageable = RecursiveMethodsUtils.getPageable(data, page, size, sortBy);
        return ResponseEntity.ok().body(storeService.findAllFilteredStores(pageable, new JSONObject(data)).getContent());
    }

}
