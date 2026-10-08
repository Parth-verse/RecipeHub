package com.recipehub.repository;

import com.recipehub.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ReviewRepository provides database CRUD operations for the Review entity.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Retrieve all reviews for a specific recipe
    List<Review> findByRecipeId(Long recipeId);

    // Retrieve all reviews submitted by a specific user
    List<Review> findByUserId(Long userId);

    // Check if a user has already reviewed a recipe
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
}
