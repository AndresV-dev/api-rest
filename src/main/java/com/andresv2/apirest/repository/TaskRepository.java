package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query(value = "Select t.*, cat.name as category from task as t inner join category cat on t.category_id = cat.id where t.user_id = :userId", nativeQuery = true)
    Page<Task> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}
