package com.andresv2.apirest.service;

import com.andresv2.apirest.entities.Task;
import com.andresv2.apirest.entities.TaskPriority;
import com.andresv2.apirest.repository.TaskPriorityRepository;
import com.andresv2.apirest.repository.TaskRepository;
import com.andresv2.apirest.util.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TaskService {

    @Autowired
    private TaskRepository taskRepo;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskPriorityRepository taskPriorityRepo;
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
/*                case "collectionId" -> {
                    UserTaskCollection collection = userService.getCollectionById(id, (Long) value);
                    if (collection.getError() != null && !Objects.equals(collection.getError(), "")){
                       throw new UsernameNotFoundException("This Category is not this user");
                    }
                taskteger.setCollection(collection);
                }
                case "categoryId" -> task.setCategory(categoryService.getCategoriesByStoreId((Long) value).get(0));
*/                case "priorityId" -> task.setPriorityId((Integer) value);
                case "userId" -> task.setUserId((Long) value);
            }
        });
        return taskRepo.save(task);
    }

    public Page<TaskPriority> getTaskPriorityList(Pageable pageable){
        return taskPriorityRepo.findAll(pageable);
    }

    public Page<Task> getListTaskByUserId(Pageable pageable, Long user_id){
        return taskRepo.findAllByUserId(user_id, pageable);
    }

    public Page<Task> getListTaskByFiltersByUserId(Pageable pageable, Long user_id, JSONObject filterData){
        JSONObject filters = new JSONObject();
        JSONObject equalsTo = new JSONObject();

        // All names of the keys reference to the keys of the Class
        if(filterData.has("collection")) equalsTo.put("collectionId", filterData.getInt("collection"));
        if(filterData.has("category")) equalsTo.put("categoryId", filterData.getInt("category"));
        if(filterData.has("priority")) equalsTo.put("priorityId", filterData.getInt("priority"));
        if (filterData.has("endAt")) equalsTo.put("endAt", Date.valueOf(filterData.getString("endAt")));

        equalsTo.put("userId", user_id);
        filters.put("equals", equalsTo);

        // if the method have more filters like between or  greater than etc. we need to add the summary of sizes to size key Example size-> filters.put("size", equalsTo.length() + greaterThan.length() + between.length());
        filters.put("size", equalsTo.length()); // + between.length()
        return taskRepo.findAll(searchUtilsTask.getQueryParameters(filters), pageable);
    }
}
