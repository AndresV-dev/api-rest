package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.CollectionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionCategoryRepository extends JpaRepository<CollectionCategory, Long>, JpaSpecificationExecutor<CollectionCategory> {


}
