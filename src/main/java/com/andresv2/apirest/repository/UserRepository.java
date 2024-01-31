package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username); //<T, ID>
    Optional<User> findByEmail(String email); //<T, ID>
    Optional<User> findByUuid(String uuid); //<T, ID>
    Optional<User> findById(Long id); //<T, ID>
    @Query(value = "UPDATE user u SET :setValues WHERE uuid = :uuid", nativeQuery = true)
    User update(@Param("setValues") List<String> setValues, @Param("uuid") String uuid);
    @Query(value = "UPDATE user set password = :newPass where uuid = :uuid and password = :oldPass;", nativeQuery = true)
    boolean updatePassword(@Param("uuid") String uuid, @Param("newPass") String newPassword, @Param("oldPass") String oldPassword);
    @Query(value = "DELETE FROM user WHERE uuid = :uuid", nativeQuery = true)
    boolean deleteByUuid (@Param("uuid") String uuid);
    Page<User> findAll(Pageable pageable); //Search without filters
    Page<User> findAll(Specification<User> specification, Pageable pageable); //Search with filters
}
