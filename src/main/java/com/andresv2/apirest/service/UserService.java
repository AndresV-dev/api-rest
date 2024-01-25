package com.andresv2.apirest.service;

import com.andresv2.apirest.dto.CredentialsDto;
import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
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

    public User findByUuid(String uuid) {
        return userRepository.findByUuid(uuid).orElseThrow(() -> {throw new UsernameNotFoundException("User with UUID " + uuid + " not found");});
    }

    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() -> {throw new UsernameNotFoundException("User with ID " + id + " not found");});
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User login(CredentialsDto credentialsDto) {
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
       throw new BadCredentialsException("Wrong username or password");
    }

    public User register(User userData) {
        Optional<User> userOptional = userRepository.findByUsername(userData.getUsername());

        if(userOptional.isPresent()){
            throw new UsernameNotFoundException("Username Already Exists");
        }

        userData.setPassword(passwordEncoder.encode(CharBuffer.wrap(userData.getPassword())));
        userData.setUuid(java.util.UUID.randomUUID().toString());

        return userRepository.save(userData);
    }

    public User update(String uuid,HashMap<String, Object> userData) {
        return userRepository.update(uuid, getQueryParameters(userData, "SET"));
    }

    public boolean updatePassword(String uuid, String newPassword, String oldPassword) {
        return userRepository.updatePassword(uuid, passwordEncoder.encode(CharBuffer.wrap(newPassword)), passwordEncoder.encode(CharBuffer.wrap(oldPassword))); //<T, ID>
    }

    public boolean delete(String uuid) {
        return userRepository.deleteByUuid(uuid); //<T, ID>
    }

    private String getQueryParameters(HashMap<String, Object> data, String type){
        final boolean[] firstLap = {true};
        data.put(type, "");

        data.forEach((key, value) -> {
            if(firstLap[0]) {
                data.put(type , key + " = " + value);
                firstLap[0] = false;
            }else{
                data.put(type, data.get(type) + ", " + key + " = " + value); //<T, ID>
            }
        });

        return data.get(type).toString(); //<T, ID>
    }
}
