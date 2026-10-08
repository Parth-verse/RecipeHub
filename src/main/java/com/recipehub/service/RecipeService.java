package com.recipehub.service;

import com.recipehub.model.Recipe;
import com.recipehub.model.User;
import com.recipehub.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RecipeService manages business logic for recipe creation, search, retrieval, updates, and deletion.
 */
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Retrieves all APPROVED recipes for the public website.
     * Supports optional search query and category filter.
     */
    public List<Recipe> getApprovedRecipes(String search, String category) {
        if (search != null && !search.trim().isEmpty()) {
            return recipeRepository.findByStatusAndTitleContainingIgnoreCase("APPROVED", search.trim());
        }
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All")) {
            return recipeRepository.findByStatusAndCategoryIgnoreCase("APPROVED", category.trim());
        }
        return recipeRepository.findByStatus("APPROVED");
    }

    /**
     * Retrieves a single recipe by its ID.
     */
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id).orElse(null);
    }

    /**
     * Adds a new recipe submitted by a Contributor.
     * Newly submitted recipes always start with status "PENDING" for Admin approval.
     */
    public Recipe createRecipe(Recipe recipe, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must be logged in to create a recipe");
        }

        recipe.setUserId(user.getId());
        recipe.setAuthorName(user.getName());
        recipe.setStatus("PENDING");
        recipe.setAverageRating(0.0);

        return recipeRepository.save(recipe);
    }

    /**
     * Updates an existing recipe.
     * Only the recipe's original author (or an ADMIN) is allowed to edit it.
     */
    public Recipe updateRecipe(Long id, Recipe updated, User user) {
        Recipe existing = recipeRepository.findById(id).orElse(null);
        if (existing == null) {
            throw new IllegalArgumentException("Recipe not found with id: " + id);
        }

        // Ownership / Admin check
        boolean isOwner = existing.getUserId() != null && existing.getUserId().equals(user.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("You are not authorized to edit this recipe");
        }

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setIngredients(updated.getIngredients());
        existing.setInstructions(updated.getInstructions());
        existing.setImageUrl(updated.getImageUrl());
        existing.setCategory(updated.getCategory());

        // When contributor modifies, recipe goes back to PENDING for admin review
        if (!isAdmin) {
            existing.setStatus("PENDING");
        }

        return recipeRepository.save(existing);
    }

    /**
     * Deletes a recipe.
     * Only the recipe owner or an ADMIN can delete it.
     */
    public boolean deleteRecipe(Long id, User user) {
        Recipe existing = recipeRepository.findById(id).orElse(null);
        if (existing == null) {
            throw new IllegalArgumentException("Recipe not found with id: " + id);
        }

        boolean isOwner = existing.getUserId() != null && existing.getUserId().equals(user.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("You are not authorized to delete this recipe");
        }

        recipeRepository.delete(existing);
        return true;
    }

    /**
     * Returns all recipes created by the given contributor (for "My Recipes" page).
     */
    public List<Recipe> getRecipesByUserId(Long userId) {
        return recipeRepository.findByUserId(userId);
    }

    /**
     * Retrieves all recipes with status "PENDING" awaiting Admin review.
     */
    public List<Recipe> getPendingRecipes() {
        return recipeRepository.findByStatus("PENDING");
    }

    /**
     * Approves a recipe by its ID.
     * Sets status to "APPROVED".
     */
    public Recipe approveRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) {
            return null;
        }
        if ("APPROVED".equalsIgnoreCase(recipe.getStatus())) {
            throw new IllegalArgumentException("Recipe is already approved");
        }
        recipe.setStatus("APPROVED");
        return recipeRepository.save(recipe);
    }

    /**
     * Rejects a recipe by its ID.
     * Sets status to "REJECTED".
     */
    public Recipe rejectRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) {
            return null;
        }
        if ("REJECTED".equalsIgnoreCase(recipe.getStatus())) {
            throw new IllegalArgumentException("Recipe is already rejected");
        }
        recipe.setStatus("REJECTED");
        return recipeRepository.save(recipe);
    }

    /**
     * Saves a recipe directly (used by review rating updates).
     */
    public Recipe save(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
}

