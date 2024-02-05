package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query(value = "select p.* from product p inner join category c on(p.category = c.name) where c.name = ?1", nativeQuery = true)
    List<Product> findAllByCategory(String name);
}
