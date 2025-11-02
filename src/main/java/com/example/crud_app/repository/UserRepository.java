package com.example.crud_app.repository;

import com.example.crud_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Search by name (case-insensitive, partial match)
    List<User> findByNameContainingIgnoreCase(String name);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

}