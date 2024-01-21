package com.andresv2.apirest.repository;

import com.andresv2.apirest.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByUsername(String username); //<T, ID>
    public Optional<User> findByEmail(String email); //<T, ID>
}
