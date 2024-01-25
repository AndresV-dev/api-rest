package com.andresv2.apirest.controller;

import com.andresv2.apirest.auth.UserAuthProvider;
import com.andresv2.apirest.dto.CredentialsDto;
import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.service.UserService;
import com.andresv2.apirest.util.AuthUtilities;
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
@RequestMapping("v1/auth")
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

    @PutMapping("/update/{uuid}")
    public ResponseEntity<User> updateUser(@PathVariable("uuid") String uuid, @RequestBody HashMap<String, Object> userData){
        User userUpdated = userService.update(uuid, userData);
        userUpdated.setToken(userAuthProvider.createToken(userUpdated.getUsername()));
        return ResponseEntity.ok(userUpdated);
    }

    @GetMapping("/delete/{uuid}")
    public ResponseEntity<User> deleteUser(@PathVariable("uuid") String uuid){
        // first you have to get user, and create a token, if the token recently generated and the token on request is the same, then you can delete the user
        User userToDelete = userService.findByUuid(uuid);
        String tokenUserDeleator = AuthUtilities.getCurrentUserToken();
        userToDelete.setToken(userAuthProvider.createToken(userToDelete.getUsername()));

        if(userToDelete.getToken().equals(tokenUserDeleator)) {
            boolean userDeleted = userService.delete(uuid);

            if(userDeleted)
                return ResponseEntity.ok(new User("User deleted successfully"));
            else
                return ResponseEntity.badRequest().body(new User("User delete failed"));
        }

        return ResponseEntity.badRequest().body(new User("Only and ADMIN or Yourself can delete this User"));
    }

    @PostMapping("/update/password/{uuid}")
    public ResponseEntity<User> updatePassword(@PathVariable("uuid") String uuid, @RequestBody @Valid HashMap<String, Object> passInfo){
        boolean updated = userService.updatePassword(uuid, (String) passInfo.get("newPassword"), (String) passInfo.get("oldPassword"));
        if(updated)
            return ResponseEntity.ok().body(new User("Password updated successfully"));

        return ResponseEntity.badRequest().body(new User("Password update failed"));
    }
}
