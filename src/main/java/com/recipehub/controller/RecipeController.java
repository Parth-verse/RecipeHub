package com.recipehub.controller;

import com.recipehub.model.Recipe;
import com.recipehub.model.User;
import com.recipehub.service.RecipeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * RecipeController handles REST endpoints for recipes.
 * Base path: /api/recipes
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Endpoint: GET /api/recipes
     * Public endpoint: Returns all APPROVED recipes.
     * Supports optional search keyword and category query parameters.
     */
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllApprovedRecipes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        List<Recipe> recipes = recipeService.getApprovedRecipes(search, category);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Endpoint: GET /api/recipes/{id}
     * Public endpoint: Returns details of a specific recipe by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Recipe not found with id: " + id));
        }
        return ResponseEntity.ok(recipe);
    }

    /**
     * Endpoint: GET /api/recipes/my
     * Protected endpoint: Returns all recipes submitted by the currently logged-in Contributor.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyRecipes(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please log in to view your recipes"));
        }

        List<Recipe> myRecipes = recipeService.getRecipesByUserId(loggedInUser.getId());
        return ResponseEntity.ok(myRecipes);
    }

    /**
     * Endpoint: POST /api/recipes
     * Protected endpoint: Allows Contributor or Admin to submit a new recipe.
     * Newly created recipes default to status "PENDING".
     */
    @PostMapping
    public ResponseEntity<?> createRecipe(@RequestBody Recipe recipe, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please log in to add a recipe"));
        }

        // Validate basic inputs
        if (recipe.getTitle() == null || recipe.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Recipe title is required"));
        }
        if (recipe.getIngredients() == null || recipe.getIngredients().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ingredients are required"));
        }
        if (recipe.getInstructions() == null || recipe.getInstructions().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Instructions are required"));
        }

        try {
            Recipe saved = recipeService.createRecipe(recipe, loggedInUser);
            return ResponseEntity.ok(Map.of(
                    "message", "Recipe submitted successfully! It is currently PENDING admin approval.",
                    "recipe", saved
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Endpoint: PUT /api/recipes/{id}
     * Protected endpoint: Update an existing recipe by its creator or Admin.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please log in to edit a recipe"));
        }

        try {
            Recipe updated = recipeService.updateRecipe(id, recipe, loggedInUser);
            return ResponseEntity.ok(Map.of(
                    "message", "Recipe updated successfully!",
                    "recipe", updated
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Endpoint: DELETE /api/recipes/{id}
     * Protected endpoint: Delete a recipe by its creator or Admin.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please log in to delete a recipe"));
        }

        try {
            recipeService.deleteRecipe(id, loggedInUser);
            return ResponseEntity.ok(Map.of("message", "Recipe deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
