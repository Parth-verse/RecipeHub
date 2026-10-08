package com.recipehub.repository;

import com.recipehub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository provides database CRUD operations for the User entity.
 * Inherits standard methods like save(), findById(), findAll(), delete() from JpaRepository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query method automatically implemented by Spring Data JPA
    Optional<User> findByEmail(String email);

    // Checks if an email is already registered in the database
    boolean existsByEmail(String email);
}
