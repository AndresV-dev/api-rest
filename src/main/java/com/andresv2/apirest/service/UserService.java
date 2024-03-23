package com.andresv2.apirest.service;

import com.andresv2.apirest.dto.CredentialsDto;
import com.andresv2.apirest.entities.CollectionCategory;
import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.entities.UserTaskCollection;
import com.andresv2.apirest.repository.CollectionCategoryRepository;
import com.andresv2.apirest.repository.CollectionRepository;
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
    private CollectionRepository collectionRepo;
    @Autowired
    private CollectionCategoryRepository collectionCategoryRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SearchUtils<User> searchUtils;
    @Autowired
    private SearchUtils<UserTaskCollection> searchUtilsCollection;
    @Autowired
    private SearchUtils<CollectionCategory> searchUtilsCollectionCategory;
    @Autowired
    private SearchUtils<CollectionCategory> categorySearchUtils;

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
        // All names of the keys reference to the keys of the Class
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

        userData.setJson(new JSONObject(userData.toString()).toString());
        userData.setPassword(passwordEncoder.encode(CharBuffer.wrap(userData.getPassword())));
        userData.setUuid(java.util.UUID.randomUUID().toString());
        userData.setCreated_at(new Date());

        return userRepository.save(userData);
    }

    public User update(String uuid,HashMap<String, Object> userData) {
        User  user = userRepository.findByUuid(uuid).orElseThrow(() -> {throw new UsernameNotFoundException("User with UUID " + uuid + " not found");});
        userData.forEach((key, value) -> {
            // All names of the keys reference to the keys of the Class
            switch (key) {
                case "name" -> user.setName((String) value);
                case "lastname" -> user.setLastname((String) value);
                case "role" -> user.setRole((String) value);
                case "age" -> user.setAge((int) value);
                case "username" -> user.setUsername((String) value);
                case "email" -> user.setEmail((String) value);
                case "json" -> user.setJson(new JSONObject(userData.toString()).toString());
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

    public UserTaskCollection saveCollection(UserTaskCollection collection){
        return collectionRepo.save(collection);
    }

    public UserTaskCollection updateCollection(Long id,HashMap<String, Object> collectionData){
        UserTaskCollection collection = collectionRepo.getReferenceById(id);

        collectionData.forEach((key, value) -> {
            // All names of the keys reference to the keys of the Class
            switch (key) {
                case "name" -> collection.setName((String) value);
                case "description" -> collection.setDescription((String) value);
                case "userId" -> collection.setUserId((Long) value);
                case "categories" -> collection.setCategories((List<CollectionCategory>) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });

        return collectionRepo.save(collection);
    }

    public CollectionCategory saveCategory(CollectionCategory collection){
        return collectionCategoryRepo.save(collection);
    }

    public CollectionCategory updateCategory(Long id,HashMap<String, Object> categoryData){
        CollectionCategory collection = collectionCategoryRepo.getReferenceById(id);

        categoryData.forEach((key, value) -> {
            // All names of the keys reference to the keys of the Class
            switch (key) {
                case "name" -> collection.setName((String) value);
                case "description" -> collection.setDescription((String) value);
                case "collection_id" -> collection.setCollection_id((Long) value);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            }
        });

        return collectionCategoryRepo.save(collection);
    }

    public Page<UserTaskCollection> getListCollection(Long userId, Pageable pageable){
        return collectionRepo.findAll(searchUtilsCollection.getQueryParameters(new JSONObject("{ 'equals' : { 'userId' : " + userId + " }, 'size' : 1}")), pageable);
    }

    public UserTaskCollection getCollectionById(Long userId, Long collectionId){
        return collectionRepo.findByIdAndUserId(collectionId, userId);
    }

    public Page<CollectionCategory> getListCategoriesFilters(Long userId, JSONObject data, Pageable pageable){

        JSONObject filters = new JSONObject();
        JSONObject equalsTo = new JSONObject();
        JSONObject likeTo = new JSONObject();
        // All names of the keys reference to the keys of the Class
        if(data.has("name")) equalsTo.put("name", data.getString("name"));
        if(data.has("category")) likeTo.put("description", data.getInt("category"));
        if(data.has("collection_id")) equalsTo.put("collection_id", data.getInt("collection_id"));

        // This is Mandatory 'cause One collection Only can be for One User
        equalsTo.put("userId", userId);

        if(equalsTo.length() > 0) filters.put("equals", equalsTo);
        if(likeTo.length() > 0) filters.put("like", likeTo);

        filters.put("size", equalsTo.length() + likeTo.length());

        return collectionCategoryRepo.findAll(categorySearchUtils.getQueryParameters(filters), pageable);
    }

    public Page<UserTaskCollection> getListCollectionFilters(Long userId, JSONObject data, Pageable pageable){

        if(data.has("category"))
            return collectionRepo.findAllByUserIdAndCategories_name(userId ,data.getString("category"), pageable);

        JSONObject filters = new JSONObject();
        JSONObject equalsTo = new JSONObject();
        JSONObject likeTo = new JSONObject();
        // All names of the keys reference to the keys of the Class
        if(data.has("name")) equalsTo.put("name", data.getString("name"));
        if(data.has("category")) likeTo.put("description", data.getInt("category"));
        if(data.has("collection_id")) equalsTo.put("collection_id", data.getInt("collection_id"));

        // This is Mandatory 'cause One collection Only can be for One User
        equalsTo.put("userId", userId);

        if(equalsTo.length() > 0) filters.put("equals", equalsTo);
        if(likeTo.length() > 0) filters.put("like", likeTo);

        filters.put("size", equalsTo.length() + likeTo.length());

        return collectionRepo.findAll(searchUtilsCollection.getQueryParameters(filters), pageable);
    }

    public Page<CollectionCategory> getListCategoriesByCollection(Long collectionId, Pageable pageable){
        return collectionCategoryRepo.findAll(searchUtilsCollectionCategory.getQueryParameters(new JSONObject("{ 'equals' : { 'collection_id' : " + collectionId + " }, 'size' : 1}")), pageable);
    }
}
