package com.recipehub.controller;

import com.recipehub.model.Recipe;
import com.recipehub.model.SavedRecipe;
import com.recipehub.model.User;
import com.recipehub.service.SavedRecipeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SavedRecipeController provides endpoints for bookmarking, unbookmarking,
 * and listing saved recipes for the current user.
 */
@RestController
@RequestMapping("/api")
public class SavedRecipeController {

    private final SavedRecipeService savedRecipeService;

    public SavedRecipeController(SavedRecipeService savedRecipeService) {
        this.savedRecipeService = savedRecipeService;
    }

    /**
     * Endpoint: GET /api/saved-recipes
     * Protected: Returns all recipes saved by the currently logged-in user.
     */
    @GetMapping("/saved-recipes")
    public ResponseEntity<?> getMySavedRecipes(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please log in to view saved recipes"));
        }

        List<Recipe> savedRecipes = savedRecipeService.getSavedRecipesForUser(loggedInUser.getId());
        return ResponseEntity.ok(savedRecipes);
    }

    /**
     * Endpoint: POST /api/recipes/{id}/save
     * Protected: Saves/bookmarks a recipe for the logged-in user.
     */
    @PostMapping("/recipes/{id}/save")
    public ResponseEntity<?> saveRecipe(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please log in to save recipes"));
        }

        try {
            SavedRecipe bookmark = savedRecipeService.saveRecipe(id, loggedInUser);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Recipe saved successfully");
            response.put("savedRecipeId", bookmark.getId());
            response.put("recipeId", id);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Endpoint: DELETE /api/recipes/{id}/save
     * Protected: Removes a recipe from the logged-in user's saved list.
     */
    @DeleteMapping("/recipes/{id}/save")
    public ResponseEntity<?> removeSavedRecipe(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please log in to remove saved recipes"));
        }

        try {
            savedRecipeService.removeSavedRecipe(id, loggedInUser);
            return ResponseEntity.ok(Map.of("message", "Recipe removed from saved collection", "recipeId", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Endpoint: GET /api/recipes/{id}/is-saved
     * Protected: Checks if the currently logged-in user has bookmarked this recipe.
     */
    @GetMapping("/recipes/{id}/is-saved")
    public ResponseEntity<?> isRecipeSaved(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.ok(Map.of("isSaved", false));
        }

        boolean saved = savedRecipeService.isRecipeSaved(id, loggedInUser.getId());
        return ResponseEntity.ok(Map.of("isSaved", saved));
    }
}
