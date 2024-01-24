package com.andresv2.apirest.controller;

import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "v1/user")
public class UserController {

    private UserService userService;

    @GetMapping("id/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long idUser) {
        return ResponseEntity.ok().body(userService.findById(idUser));
    }

    @GetMapping("uuid/{uuid}")
    public ResponseEntity<User> getUserByUuid(@PathVariable("uuid") String uuidUser) {
        return ResponseEntity.ok(userService.findByUuid(uuidUser));
    }

    @PostMapping("list")
    public ResponseEntity<List<User>> getUserList(){
        return ResponseEntity.ok().body(userService.findAll());
    }
}
