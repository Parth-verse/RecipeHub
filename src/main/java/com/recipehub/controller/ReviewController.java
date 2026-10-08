package com.recipehub.controller;

import com.recipehub.model.Review;
import com.recipehub.model.User;
import com.recipehub.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReviewController exposes endpoints for adding, fetching, and deleting recipe reviews.
 */
@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Endpoint: GET /api/recipes/{recipeId}/reviews
     * Public: Returns all reviews for the specified recipe.
     */
    @GetMapping("/recipes/{recipeId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByRecipeId(@PathVariable Long recipeId) {
        List<Review> reviews = reviewService.getReviewsByRecipeId(recipeId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Endpoint: POST /api/recipes/{recipeId}/reviews
     * Protected: Allows a logged-in user (e.g. Explorer) to submit a rating and comment.
     */
    @PostMapping("/recipes/{recipeId}/reviews")
    public ResponseEntity<?> addReview(
            @PathVariable Long recipeId,
            @RequestBody Review review,
            HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please log in to submit a review"));
        }

        try {
            Review savedReview = reviewService.addReview(recipeId, review, loggedInUser);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Review submitted successfully");
            response.put("review", savedReview);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Endpoint: DELETE /api/reviews/{id}
     * Protected: Delete a review (allowed for the review author or Admin).
     */
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Please log in to delete a review"));
        }

        try {
            reviewService.deleteReview(id, loggedInUser);
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
