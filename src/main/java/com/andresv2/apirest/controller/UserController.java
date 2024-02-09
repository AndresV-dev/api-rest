package com.andresv2.apirest.controller;

import com.andresv2.apirest.entities.CollectionCategory;
import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.entities.UserTaskCollection;
import com.andresv2.apirest.service.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "v1/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @GetMapping("id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long idUser) {
        return ResponseEntity.ok().body(userService.findById(idUser));
    }

    @GetMapping("uuid/{uuid}")
    public ResponseEntity<User> getUserByUuid(@PathVariable("uuid") String uuidUser) {
        return ResponseEntity.ok(userService.findByUuid(uuidUser));
    }

    @GetMapping("list") // Optional RequestParams, can call endpoint "user/list" or "user/list?page=1&size=10"
    public ResponseEntity<List<User>> getUserListSorted(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size){
//        logger.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        return ResponseEntity.ok().body(userService.findAll(pageable).getContent());
    }

    @PostMapping("filtered/list") // Optional RequestParams, can call endpoint "user/list" or "user/list?page=1&size=10"
    public ResponseEntity<List<User>> getUserFilteredListSorted(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestBody HashMap<String, Object> data){
//        logger.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        return ResponseEntity.ok().body(userService.findUsersByFilters(pageable, new JSONObject(data)).getContent());
    }
    @GetMapping("collection/list")
    public ResponseEntity<List<UserTaskCollection>> getListCollectionByUserId(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        return ResponseEntity.ok(userService.getListCollection(user.getId(), pageable).getContent());
    }

    @PostMapping("collection/list")
    public ResponseEntity<List<UserTaskCollection>> getListCollectionByUserIdFilters(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,@RequestBody HashMap<String, Object> data) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        return ResponseEntity.ok(userService.getListCollectionFilters(user.getId(), new JSONObject(data), pageable).getContent());
    }

    @GetMapping("categories/collection/{id}")
    public ResponseEntity<List<CollectionCategory>> getListCategoriesByCollection(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @PathVariable("id") Long collectionId) {
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        return ResponseEntity.ok(userService.getListCategoriesByCollection(collectionId, pageable).getContent());
    }

}
