package com.andresv2.apirest.controller;

import com.andresv2.apirest.auth.UserAuthProvider;
import com.andresv2.apirest.dto.CredentialsDto;
import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
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
    public ResponseEntity<User> register(@RequestBody @Valid User userData){
        User user = userService.register(userData);
        user.setToken(userAuthProvider.createToken(userData.getUsername()));
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);
    }

    @PostMapping("/update/{uuid}")
    public ResponseEntity<User> updateUser(@PathVariable("uuid") String uuid, @RequestBody @Valid HashMap<String, Object> userData){
        User userUpdated = userService.update(uuid, userData);
        userUpdated.setToken(userAuthProvider.createToken(userUpdated.getUsername()));
        return ResponseEntity.ok(userUpdated);
    }

    @PostMapping("/update/password/{uuid}")
    public ResponseEntity<User> updatePassword(@PathVariable("uuid") String uuid, @RequestBody @Valid HashMap<String, Object> passInfo){
        boolean updated = userService.updatePassword(uuid, (String) passInfo.get("newPassword"), (String) passInfo.get("oldPassword"));
        if(updated)
            return ResponseEntity.ok().body(new User("Password updated successfully"));

        return ResponseEntity.badRequest().body(new User("Password update failed"));
    }
}
