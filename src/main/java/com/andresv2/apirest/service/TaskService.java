package com.andresv2.apirest.service;

import com.andresv2.apirest.entities.Task;
import com.andresv2.apirest.repository.TaskRepository;
import com.andresv2.apirest.util.SearchUtils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    @Autowired
    private TaskRepository taskRepo;
    @Autowired
    private SearchUtils<Task> searchUtilsTask;

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

        equalsTo.put("user_id", user_id);
        filters.put("equals", equalsTo);

        // if the method have more filters like between or  greater than etc. we need to add the summary of sizes to size key Example size-> filters.put("size", equalsTo.length() + greaterThan.length() + between.length());
        filters.put("size", equalsTo.length()); // + between.length()

        return taskRepo.findAll(searchUtilsTask.getQueryParameters(filters), pageable);
    }
}
