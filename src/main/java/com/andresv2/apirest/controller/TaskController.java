package com.andresv2.apirest.controller;

import com.andresv2.apirest.entities.Task;
import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.service.TaskService;
import com.andresv2.apirest.util.AuthUtilities;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = "v1/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PutMapping("register")
    public ResponseEntity<Task> saveTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.saveTask(task));
    }

    @PostMapping("update")
    public ResponseEntity<Task> updateTask(@Valid @RequestBody HashMap<String, Object> taskData, @RequestParam("id") Long id) {
        return ResponseEntity.ok(taskService.updateTask(id, taskData));
    }

    @GetMapping("list")
    public ResponseEntity<List<Task>> getListTask(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        Page<Task> a = taskService.getListTaskByUserId(pageable, AuthUtilities.getCurrentUser().getId());
        return ResponseEntity.ok(a.getContent());
    }

    @PostMapping("list/filtered")
    public ResponseEntity<List<Task>> getListTaskByCollection(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestBody HashMap<String, Object> filterData) {
        Pageable pageable = PageRequest.of(page!=null?page:0, size!=null?size:10, Sort.by("id").descending());
        return ResponseEntity.ok(taskService.getListTaskByFiltersByUserId(pageable,  AuthUtilities.getCurrentUser().getId(), new JSONObject(filterData)).getContent());
    }
}
