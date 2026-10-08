package com.recipehub.service;

import com.recipehub.model.Recipe;
import com.recipehub.model.SavedRecipe;
import com.recipehub.model.User;
import com.recipehub.repository.RecipeRepository;
import com.recipehub.repository.SavedRecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * SavedRecipeService handles business logic for bookmarking, removing,
 * and listing saved recipes for users.
 */
@Service
public class SavedRecipeService {

    private final SavedRecipeRepository savedRecipeRepository;
    private final RecipeRepository recipeRepository;

    public SavedRecipeService(SavedRecipeRepository savedRecipeRepository, RecipeRepository recipeRepository) {
        this.savedRecipeRepository = savedRecipeRepository;
        this.recipeRepository = recipeRepository;
    }

    /**
     * Saves a recipe to the user's bookmarks.
     * Prevents duplicate saves.
     */
    public SavedRecipe saveRecipe(Long recipeId, User user) {
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found with id: " + recipeId);
        }

        if (savedRecipeRepository.existsByUserIdAndRecipeId(user.getId(), recipeId)) {
            throw new IllegalArgumentException("Recipe is already saved in your collection");
        }

        SavedRecipe saved = new SavedRecipe(user.getId(), recipeId);
        return savedRecipeRepository.save(saved);
    }

    /**
     * Removes a recipe from the user's bookmarks.
     */
    @Transactional
    public boolean removeSavedRecipe(Long recipeId, User user) {
        SavedRecipe bookmark = savedRecipeRepository.findByUserIdAndRecipeId(user.getId(), recipeId).orElse(null);
        if (bookmark == null) {
            throw new IllegalArgumentException("Recipe is not in your saved collection");
        }

        savedRecipeRepository.delete(bookmark);
        return true;
    }

    /**
     * Retrieves the full Recipe objects that the user has saved.
     * This allows the "My Saved Recipes" page to directly display recipe cards.
     */
    public List<Recipe> getSavedRecipesForUser(Long userId) {
        List<SavedRecipe> bookmarks = savedRecipeRepository.findByUserId(userId);
        List<Recipe> recipes = new ArrayList<>();

        for (SavedRecipe sr : bookmarks) {
            Recipe recipe = recipeRepository.findById(sr.getRecipeId()).orElse(null);
            if (recipe != null) {
                recipes.add(recipe);
            }
        }

        return recipes;
    }

    /**
     * Checks if a specific recipe is saved by the given user.
     */
    public boolean isRecipeSaved(Long recipeId, Long userId) {
        return savedRecipeRepository.existsByUserIdAndRecipeId(userId, recipeId);
    }
}
