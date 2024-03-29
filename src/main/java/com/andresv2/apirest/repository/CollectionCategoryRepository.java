package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.CollectionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionCategoryRepository extends JpaRepository<CollectionCategory, Long>, JpaSpecificationExecutor<CollectionCategory> {

    @Query(value = "Select * from collection_category where id in(?1)", nativeQuery = true)
    Page<CollectionCategory> findAll(List<Long> categoriesIds, Pageable pageable);
}
