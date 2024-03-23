package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.UserTaskCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<UserTaskCollection, Long>, JpaSpecificationExecutor<UserTaskCollection> {

    Page<UserTaskCollection> findAllByUserIdAndCategories_name(Long user_id, String name, Pageable pageable);
    UserTaskCollection findByIdAndUserId(Long id, Long user_id);
}
