package com.andresv2.apirest.repository;

import com.andresv2.apirest.dto.CollectionCatDto;
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
    @Query(value = "Select new CollectionCatDto(t.id, utc.name collection, cc.name category, count(t.categoryId) registers) from Task t, CollectionCategory cc, UserTaskCollection utc where cc.id = t.categoryId and utc.id = cc.collection_id and t.userId = ?1 group by t.categoryId")
    List<CollectionCatDto> getTasksChartsWithCategory(Long userId);
    @Query(value = "select new CollectionCatDto(t.id, utc.name collection, count(t.id) registers) from Task t, UserTaskCollection utc where utc.id = t.collectionId and t.userId = ?1 group by t.collectionId")
    List<CollectionCatDto> getTasksChartsWithoutCategory(Long userId);
}
