package com.recipehub.repository;

import com.recipehub.model.SavedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SavedRecipeRepository provides database operations for recipe bookmarks.
 */
@Repository
public interface SavedRecipeRepository extends JpaRepository<SavedRecipe, Long> {

    // Retrieve all saved recipe entries for a user
    List<SavedRecipe> findByUserId(Long userId);

    // Find a specific bookmark for a user and recipe
    Optional<SavedRecipe> findByUserIdAndRecipeId(Long userId, Long recipeId);

    // Check if a user has already bookmarked a specific recipe
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);

    // Delete a specific bookmark
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
}
