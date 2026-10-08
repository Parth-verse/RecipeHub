package com.recipehub.repository;

import com.recipehub.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RecipeRepository provides database operations for Recipe entities.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // Retrieve recipes by status (e.g. all APPROVED recipes for public browsing)
    List<Recipe> findByStatus(String status);

    // Search approved recipes by title keyword (case-insensitive)
    List<Recipe> findByStatusAndTitleContainingIgnoreCase(String status, String title);

    // Filter approved recipes by category
    List<Recipe> findByStatusAndCategoryIgnoreCase(String status, String category);

    // Retrieve all recipes created by a specific user (for Contributor's "My Recipes")
    List<Recipe> findByUserId(Long userId);

    // Check if a recipe with the specified title already exists (used for safe DataLoader seeding)
    boolean existsByTitle(String title);

    // Find a recipe by exact title
    Optional<Recipe> findByTitle(String title);
}
