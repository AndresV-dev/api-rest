package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username); //<T, ID>
    Optional<User> findByEmail(String email); //<T, ID>
    Optional<User> findByUuid(String uuid); //<T, ID>

    @Query(value = "UPDATE User u SET ?1 WHERE ?2")
    User update(String setValues, String uuid);

    @Query(value = "UPDATE user set password = ?2 where uuid = ?1 and password = ?3;")
    boolean updatePassword(String uuid, String newPassword, String oldPassword);
}
