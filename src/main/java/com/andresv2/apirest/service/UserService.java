package com.andresv2.apirest.service;

import com.andresv2.apirest.dto.CredentialsDto;
import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findByUsername(String  username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown User [" + username + "]"));
    }

    public User login(CredentialsDto credentialsDto) {
       try {
           User user;
           if(credentialsDto.getEmail() == null && credentialsDto.getUsername() == null)
               return new User("Please give a Username or Email for logging in");

           if(credentialsDto.getUsername() != null) // validate Username
               user = userRepository.findByUsername(credentialsDto.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User Doesn't Exist"));
           else // Validate Email if Username is null
              user = userRepository.findByEmail(credentialsDto.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User Doesn't Exist"));

           if(passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())){
               return user;
           }
       }catch (Exception e) {
           return new User(e.getMessage());
       }
       return new User("Invalid Password");
    }

    public User register(User userData) {
        Optional<User> userOptional = userRepository.findByUsername(userData.getUsername());

        if(userOptional.isPresent()){
            return new User("User already exists");
        }

        userData.setPassword(passwordEncoder.encode(CharBuffer.wrap(userData.getPassword())));
        userData.setUuid(java.util.UUID.randomUUID().toString());

        return userRepository.save(userData);
    }
}
