package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query(value = "Select * from task t where t.user_id = :userId", nativeQuery = true)
    Page<Task> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
    @Query(value = "Select * from task t where user_id = :userId and end_at = :endAt", nativeQuery = true)
    Page<Task> findAllByUserIdAndEndAt(@Param("userId") Long userId, @Param("endAt") String date, Pageable pageable);
    @Query(value = "select t.id, utc.name collection, cc.name category, count(t.category_id) registers from task t inner join collection_category cc on(cc.id = t.category_id) left join user_task_collections utc on(utc.id = cc.collection_id) where t.user_id = :userId group by t.category_id;", nativeQuery = true)
    List<Task> getTasksChartsWithCategory(@Param("userId") Long userId);
    @Query(value = "select t.id, utc.name collection, count(t.id) registers from task t inner join user_task_collections utc on(utc.id = t.collection_id) where t.user_id = :userId group by t.collection_id;", nativeQuery = true)
    List<Task> getTasksChartsWithoutCategory(@Param("userId") Long userId);
}
