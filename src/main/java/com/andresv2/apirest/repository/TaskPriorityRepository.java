package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Long>, JpaSpecificationExecutor<TaskPriority> {

}
