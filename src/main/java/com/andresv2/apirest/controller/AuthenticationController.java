package com.andresv2.apirest.controller;

import com.andresv2.apirest.auth.UserAuthProvider;
import com.andresv2.apirest.dto.CredentialsDto;
import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserAuthProvider userAuthProvider;

    @PostMapping("/token")
    public ResponseEntity<User> authenticate(@RequestBody CredentialsDto userData){
        try {
            User user = userService.login(userData);

            if(user.getError() != null)
                return ResponseEntity.status(403).body(user);

            user.setToken(userAuthProvider.createToken(userData.getUsername()));
            return ResponseEntity.ok(user);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(403).body(new User(e.getCause().getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User userData){
        User user = userService.register(userData);
        user.setToken(userAuthProvider.createToken(userData.getUsername()));
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);
    }
}
