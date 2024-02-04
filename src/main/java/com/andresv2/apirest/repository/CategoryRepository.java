package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    @Query(value = "select * from category c left join store_categories sc on(sc.category_id = c.id) where sc.store_id = ?1", nativeQuery = true)
    List<Category> getCategoriesByStoreId(Long storeId);
}
