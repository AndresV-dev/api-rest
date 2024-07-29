package com.andresv2.apirest;

import com.andresv2.apirest.entities.*;
import com.andresv2.apirest.repository.CollectionRepository;
import com.andresv2.apirest.service.CategoryService;
import com.andresv2.apirest.service.TaskService;
import com.andresv2.apirest.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApirestApplicationTests {

	@Autowired
	TaskService taskService;
	@Autowired
	UserService userService;
	@Autowired
	CollectionRepository collectionRepository;
	@Test
	void contextLoads() {
	}

	@Test
	void savedUserHasRegistrationDate() {
		// Get the User to insert him the tasks
		User user = userService.findById(11);

		// Set some variables for the creation of the Tasks
		Calendar endDateTask = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// Define the variables used by the process and the task that will be created
		int daysCountForCreateTasks = 10, idCategory = 0, idCollection = 0, idPriority = 0, taskCreatedSameCollectionAndCategory = 0;
		// Get the Collections of the user
		List<UserTaskCollection> collection = collectionRepository.findAllByUserId(user.getId());
		// Get the Priorities Catalog
		List<TaskPriority> priorities = taskService.getTaskPriorityList(PageRequest.of(0, 10, Sort.by("id").descending())).getContent();

		List<Task> savedTaskList = new ArrayList<>();

		for (int i = 0; i < daysCountForCreateTasks; i++) {
			// When the priority List has been fully read it will start over on 0
			if(idPriority == priorities.size())
				idPriority = 0;

			// When the category List has been fully read and this object is not empty,it will start over on 0 and idCollection will increse on 1
			if(idCategory == collection.get(idCollection).getCategories().size() && collection.get(idCollection).getCategories().size() != 0) {
				idCategory = 0;
				idCollection =+ idCollection + 1;
				taskCreatedSameCollectionAndCategory = 0;
			}
			// When the process has been generated 2 task of the same collection/category it will change the collection
			if(taskCreatedSameCollectionAndCategory > 1) {
				idCollection = +idCollection + 1;
				taskCreatedSameCollectionAndCategory = 0;
			}

			if(idCollection == collection.size())
				idCollection = 0;

			Task task = new Task(
					Long.parseLong("1"),
					UUID.randomUUID().toString(),
					"Nueva",
					("Tarea " + collection.get(idCollection).getName() + ":" + (collection.get(idCollection).getCategories().size() != 0 ? collection.get(idCollection).getCategories().get(idCategory).getName() : "General")),
					("This is a Test Task, No." + (i + 1) + " Date :" + LocalDate.now()),
					LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId()),
					LocalDateTime.ofInstant(endDateTask.toInstant(), TimeZone.getTimeZone("UTC").toZoneId()),
					Integer.parseInt(collection.get(idCollection).getId().toString()),
					Integer.parseInt(collection.get(idCollection).getCategories().size() != 0 ? collection.get(idCollection).getCategories().get(idCategory).getId().toString() : "1"),
					Integer.parseInt(priorities.get(idPriority).getId().toString()),
					user.getId(),
					null,
					null,
					null
					);

			endDateTask.add(Calendar.DATE, 1);
			if(collection.get(idCollection).getCategories().size() != 0)
				idCategory=+ idCategory + 1;

			// Add on 1 when a task is inserted this will become 0 again if is a different collection/category
			taskCreatedSameCollectionAndCategory =+ taskCreatedSameCollectionAndCategory + 1;
			idPriority=+ idPriority + 1;
			savedTaskList.add(taskService.saveTask(task));
		}

		System.out.println(savedTaskList.size());
		assertThat(savedTaskList.size()).isGreaterThan(0);
	}
}
