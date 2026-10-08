package com.recipehub.service;

import com.recipehub.model.User;
import com.recipehub.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserService contains business logic for user management, registration, and login.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection (Spring automatically injects repository and passwordEncoder)
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     * 1. Checks if email is already registered.
     * 2. Sets default role to EXPLORER if not specified.
     * 3. Hashes the plain-text password using BCrypt.
     * 4. Saves the user to the database.
     */
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + user.getEmail());
        }

        // Validate and normalize role (default to EXPLORER)
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("EXPLORER");
        } else {
            String roleUpper = user.getRole().trim().toUpperCase();
            if (roleUpper.equals("ADMIN") || roleUpper.equals("CONTRIBUTOR") || roleUpper.equals("EXPLORER")) {
                user.setRole(roleUpper);
            } else {
                user.setRole("EXPLORER");
            }
        }

        // Hash the plain-text password before saving
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        return userRepository.save(user);
    }

    /**
     * Authenticates a user by email and password.
     * 1. Finds the user by email in the database.
     * 2. Uses BCrypt's matches() method to compare raw password with the hashed password.
     * 3. Returns the user object on success, or throws an error on failure.
     */
    public User login(String email, String rawPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // Check if user exists
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = optionalUser.get();

        // Check if entered password matches the stored BCrypt hash
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }

    // Find a user by their email address
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Find a user by their database ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Retrieve all users (for Admin)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
