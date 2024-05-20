package com.andresv2.apirest.service;

import com.andresv2.apirest.dto.CollectionCatDto;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.time.LocalDateTime;

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
                case "endAt" -> task.setEndAt((LocalDateTime) value);
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

    public List<CollectionCatDto> getTasksCharts(Long userId, Boolean withCategory){
        if(withCategory)
            return taskRepo.getTasksChartsWithCategory(userId);

        return taskRepo.getTasksChartsWithoutCategory(userId);
    };
    public Page<Task> getListTaskByUserId(Pageable pageable, Long user_id){
        return taskRepo.findAllByUserId(user_id, pageable);
    }

    public Page<Task> getListTaskByFiltersByUserId(Pageable pageable, Long user_id, JSONObject filterData) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"));

        JSONObject filters = new JSONObject();
        JSONObject equalsTo = new JSONObject();
        JSONObject between = new JSONObject();
        JSONObject like = new JSONObject();
        JSONObject greaterThanOrEqualTo = new JSONObject();

        // All names of the keys reference to the keys of the Class
        if(filterData.has("collection") && filterData.getInt("collection") != 0) equalsTo.put("collectionId", filterData.getInt("collection"));
        if(filterData.has("category") && filterData.getInt("category") != 0) equalsTo.put("categoryId", filterData.getInt("category"));
        if(filterData.has("priority") && filterData.getInt("priority") != 0) equalsTo.put("priorityId", filterData.getInt("priority"));
        if(filterData.has("description") && !filterData.getString("description").isBlank()) like.put("description", filterData.getString("description"));
        if(filterData.has("title") && !filterData.getString("title").isBlank()) like.put("title", filterData.getString("title"));
        if(filterData.has("status") && !filterData.getString("status").isBlank()) equalsTo.put("status", filterData.getString("status"));

        if (filterData.has("createdAt")) {
            if(filterData.getString("endAt").length() < 11){
                List<String> betweenDates = Arrays.asList(filterData.getString("createdAt") + "T00:00:00" , filterData.getString("endAt") + "T23:59:59");
                between.put("endAt", betweenDates);
            }else {
                String[] dateAndTime = filterData.getString("createdAt").split(" ");
                String[] date = dateAndTime[0].split("-");
                String[] time = dateAndTime[1].split(":");
                cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

                Date date1 = cal.getTime();
                greaterThanOrEqualTo.put("createdAt", sdf.format(date1).replace(" ", "T"));
            }
        }
        if (filterData.has("endAt")) {
            if(filterData.getString("endAt").length() < 11){
                List<String> betweenDates = Arrays.asList( filterData.getString("endAt") + "T00:00:00" , filterData.getString("endAt") + "T23:59:59");
                between.put("endAt", betweenDates);
            }else {
                String[] dateAndTime = filterData.getString("endAt").split(" ");
                String[] date = dateAndTime[0].split("-");
                String[] time = dateAndTime[1].split(":");
                cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

                Date date1 = cal.getTime();
                greaterThanOrEqualTo.put("endAt", sdf.format(date1).replace(" ", "T"));
            }
        }

        equalsTo.put("userId", user_id);
        filters.put("equals", equalsTo);
        filters.put("size", equalsTo.length()); // + between.length()

        if(between.length() > 0) filters.put("between", between); filters.put("size", filters.getInt("size") + between.length());
        if(like.length() > 0) filters.put("like", like); filters.put("size", filters.getInt("size") + like.length());
        if(greaterThanOrEqualTo.length() > 0) filters.put("greaterThanOrEqualTo", greaterThanOrEqualTo); filters.put("size", filters.getInt("size") + greaterThanOrEqualTo.length());

        // if the method have more filters like between or  greater than etc. we need to add the summary of sizes to size key Example size-> filters.put("size", equalsTo.length() + greaterThan.length() + between.length());
        return taskRepo.findAll(searchUtilsTask.getQueryParameters(filters), pageable);
    }
}
