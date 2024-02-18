package com.andresv2.apirest.service;

import com.andresv2.apirest.entities.Category;
import com.andresv2.apirest.entities.Task;
import com.andresv2.apirest.repository.TaskRepository;
import com.andresv2.apirest.util.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TaskService {

    @Autowired
    private TaskRepository taskRepo;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SearchUtils<Task> searchUtilsTask;

    public Task saveTask(Task task) {
        return taskRepo.save(task);
    }

    public Task updateTask(Long id, HashMap<String, Object> taskData) {
        Task task = taskRepo.findById(id).orElseThrow(() -> {throw new UsernameNotFoundException("Task with ID " + id + " not found");});
        taskData.forEach((key, value) -> {
            // All names of the keys reference to the keys of the Class
            switch (key) {
                case "title" -> task.setTitle((String) value);
                case "description" -> task.setDescription((String) value);
                case "endAt" -> task.setEndAt((Date) value);
                case "collectionId" -> task.setCollectionId((Integer) value);
                case "categoryId" -> task.setCategory(categoryService.getCategoriesByStoreId((Long) value).get(0));
                case "priorityId" -> task.setPriorityId((Integer) value);
                case "userId" -> task.setUserId((Long) value);
            }
        });
        return taskRepo.save(task);
    }

    public Page<Task> getListTaskByUserId(Pageable pageable, Long user_id){
        return taskRepo.findAllByUserId(user_id, pageable);
    }

    public Page<Task> getListTaskByFiltersByUserId(Pageable pageable, Long user_id, JSONObject filterData){
        JSONObject filters = new JSONObject();
        JSONObject equalsTo = new JSONObject();

        // All names of the keys reference to the keys of the Class
        if(filterData.has("collection")) equalsTo.put("collection", filterData.getInt("collection"));
        if(filterData.has("category")) equalsTo.put("category", filterData.getInt("category"));
        if(filterData.has("status")) equalsTo.put("priority", filterData.getInt("priority"));

        equalsTo.put("userId", user_id);
        filters.put("equals", equalsTo);

        // if the method have more filters like between or  greater than etc. we need to add the summary of sizes to size key Example size-> filters.put("size", equalsTo.length() + greaterThan.length() + between.length());
        filters.put("size", equalsTo.length()); // + between.length()

        return taskRepo.findAll(searchUtilsTask.getQueryParameters(filters), pageable);
    }
}
