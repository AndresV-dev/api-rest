package com.andresv2.apirest.controller;

import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("list")
    public ResponseEntity<List<User>> getUserListSorted(){
        logger.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        return ResponseEntity.ok().body(userService.findAll());
    }
}
