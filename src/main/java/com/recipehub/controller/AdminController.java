package com.recipehub.controller;

import com.recipehub.model.Recipe;
import com.recipehub.model.User;
import com.recipehub.service.RecipeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminController handles moderation and administrative endpoints.
 * Base path: /api/admin
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RecipeService recipeService;

    public AdminController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Helper method to verify that the current session user has the ADMIN role.
     * Returns 401 if unauthenticated, 403 if authenticated but not ADMIN, or null if authorized.
     */
    private ResponseEntity<?> checkAdminAuthorization(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please log in"));
        }
        if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied: Admin role required"));
        }
        return null; // Authorized
    }

    /**
     * Endpoint: GET /api/admin/recipes/pending
     * Returns all recipes currently awaiting Admin review.
     * Restricted to ADMIN users only.
     */
    @GetMapping("/recipes/pending")
    public ResponseEntity<?> getPendingRecipes(HttpSession session) {
        ResponseEntity<?> authCheck = checkAdminAuthorization(session);
        if (authCheck != null) {
            return authCheck;
        }

        List<Recipe> pending = recipeService.getPendingRecipes();
        return ResponseEntity.ok(pending);
    }

    /**
     * Endpoint: PUT /api/admin/recipes/{id}/approve
     * Approves a recipe so it becomes publicly visible.
     * Restricted to ADMIN users only.
     */
    @PutMapping("/recipes/{id}/approve")
    public ResponseEntity<?> approveRecipe(@PathVariable Long id, HttpSession session) {
        ResponseEntity<?> authCheck = checkAdminAuthorization(session);
        if (authCheck != null) {
            return authCheck;
        }

        try {
            Recipe approved = recipeService.approveRecipe(id);
            if (approved == null) {
                return ResponseEntity.status(404).body(Map.of("message", "Recipe not found with id: " + id));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Recipe approved successfully");
            response.put("id", approved.getId());
            response.put("status", approved.getStatus());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Endpoint: PUT /api/admin/recipes/{id}/reject
     * Rejects a recipe.
     * Restricted to ADMIN users only.
     */
    @PutMapping("/recipes/{id}/reject")
    public ResponseEntity<?> rejectRecipe(@PathVariable Long id, HttpSession session) {
        ResponseEntity<?> authCheck = checkAdminAuthorization(session);
        if (authCheck != null) {
            return authCheck;
        }

        try {
            Recipe rejected = recipeService.rejectRecipe(id);
            if (rejected == null) {
                return ResponseEntity.status(404).body(Map.of("message", "Recipe not found with id: " + id));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Recipe rejected successfully");
            response.put("id", rejected.getId());
            response.put("status", rejected.getStatus());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
