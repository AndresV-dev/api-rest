package com.andresv2.apirest.controller;

import com.andresv2.apirest.dto.CollectionCatDto;
import com.andresv2.apirest.entities.Task;
import com.andresv2.apirest.entities.TaskPriority;
import com.andresv2.apirest.entities.result.Result;
import com.andresv2.apirest.service.TaskService;
import com.andresv2.apirest.util.AuthUtilities;
import com.andresv2.apirest.util.RecursiveMethodsUtils;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PutMapping("register")
    public ResponseEntity<Task> saveTask(@Valid @RequestBody Task task) {
        return ResponseEntity.ok(taskService.saveTask(task));
    }

    @PostMapping("update")
    public ResponseEntity<Task> updateTask(@Valid @RequestBody HashMap<String, Object> taskData, @RequestParam("id") Long id) {
        return ResponseEntity.ok(taskService.updateTask(id, taskData));
    }

    @GetMapping("list")
    public ResponseEntity<List<Task>> getListTask(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortBy", required = false) String sortBy, @RequestBody HashMap<String, Object> filterData) {
        Pageable pageable = RecursiveMethodsUtils.getPageable(filterData, page, size, sortBy);
        Page<Task> a = taskService.getListTaskByUserId(pageable, AuthUtilities.getCurrentUser().getId());
        return ResponseEntity.ok(a.getContent());
    }

    @PostMapping("list/filtered")
    public ResponseEntity<List<Task>> getListTaskByCollection(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortBy", required = false) String sortBy, @RequestBody HashMap<String, Object> filterData) {
        try {

            Pageable pageable = RecursiveMethodsUtils.getPageable(filterData, page, size, sortBy);
            return ResponseEntity.ok(taskService.getListTaskByFiltersByUserId(pageable,  AuthUtilities.getCurrentUser().getId(), new JSONObject(filterData)).getContent());
        }catch (Exception e){e.printStackTrace();}
        return ResponseEntity.internalServerError().body(new ArrayList<>());
    }

    @GetMapping("priority/list")
    public ResponseEntity<List<TaskPriority>> getTaskPriorityList(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortBy", required = false) String sortBy, @RequestBody HashMap<String, Object> filterData){
        try {
            Pageable pageable = RecursiveMethodsUtils.getPageable(filterData, page, size, sortBy);
            return ResponseEntity.ok(taskService.getTaskPriorityList(pageable).getContent());
        }catch (Exception e){e.printStackTrace();}
        return ResponseEntity.internalServerError().body(new ArrayList<>());
    }

    @PostMapping("/charts")
    public ResponseEntity<List<CollectionCatDto>> getTasksChart(@RequestBody HashMap<String, Object> filterData){
        return ResponseEntity.ok(taskService.getTasksCharts(AuthUtilities.getCurrentUser().getId(), (Boolean) filterData.get("categories")));
    }

    @PostMapping("/delete")
    public ResponseEntity<Object> deleteTaskFromUser(@RequestBody HashMap<String, Object> data) {
        Result<Task> taskResult = taskService.deleteTasksByUserId(AuthUtilities.getCurrentUser().getId(), (List<Integer>) data.get("taskIds"));
        return taskResult.isSuccessful() ? ResponseEntity.ok("All Tasks has been Deleted") : ResponseEntity.internalServerError().body(new JSONObject("{'error': '" + taskResult.getErrorMessage() + "'}"));
    }
}
