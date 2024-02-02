package com.andresv2.apirest.service;

import com.andresv2.apirest.dto.CredentialsDto;
import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.repository.UserRepository;
import com.andresv2.apirest.util.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private SearchUtils<User> searchUtils;

    public User findByUsername(String  username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Unknown User [" + username + "]"));
    }

    public User findByUuid(String uuid) {
        return userRepository.findByUuid(uuid).orElseThrow(() -> {throw new UsernameNotFoundException("User with UUID " + uuid + " not found");});
    }

    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() -> {throw new UsernameNotFoundException("User with ID " + id + " not found");});
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> findUsersByFilters(Pageable pageable, JSONObject userData) {
        JSONObject filters = new JSONObject();
        JSONObject equalsTo = new JSONObject();
//        JSONObject between = new JSONObject(); //only if filters have between option
        // Define Filters
        if (userData.has("name")) equalsTo.put("name", userData.getString("name"));
        if (userData.has("lastname")) equalsTo.put("lastname", userData.getString("lastname"));
        if (userData.has("role")) equalsTo.put("role", userData.getString("role"));
        if (userData.has("age")) equalsTo.put("age", userData.getInt("age"));
        if (userData.has("username")) equalsTo.put("username", userData.getString("username"));
        if (userData.has("email")) equalsTo.put("email", userData.getString("email"));

        if(equalsTo.length() > 0) filters.put("equals", equalsTo);
//        if(between.length() > 0) filters.put("between", between); //only if filters have between option

        // if the method have more filters like between or  greater than etc. we need to add the summary of sizes to size key Example size-> filters.put("size", equalsTo.length() + greaterThan.length() + between.length());
        filters.put("size", equalsTo.length()); // + between.length()
        return userRepository.findAll(searchUtils.getQueryParameters(filters), pageable);
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
        userData.setCreated_at(new Date());

        return userRepository.save(userData);
    }

    public User update(String uuid,HashMap<String, Object> userData) {
        User  user = userRepository.findByUuid(uuid).orElseThrow(() -> {throw new UsernameNotFoundException("User with UUID " + uuid + " not found");});
        userData.forEach((key, value) -> {
            switch (key) {
                case "name" -> user.setName((String) value);
                case "lastname" -> user.setLastname((String) value);
                case "role" -> user.setRole((String) value);
                case "age" -> user.setAge((int) value);
                case "username" -> user.setUsername((String) value);
                case "email" -> user.setEmail((String) value);
                case "json" -> user.setJson((String) value);
            }
        });
         return userRepository.save(user);
    }

    public boolean updatePassword(String uuid, String newPassword, String oldPassword) {
        return userRepository.updatePassword(uuid, passwordEncoder.encode(CharBuffer.wrap(newPassword)), passwordEncoder.encode(CharBuffer.wrap(oldPassword))); //<T, ID>
    }

    public boolean delete(String uuid) {
        return userRepository.deleteByUuid(uuid); //<T, ID>
    }

}
