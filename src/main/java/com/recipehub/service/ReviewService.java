package com.recipehub.service;

import com.recipehub.model.Recipe;
import com.recipehub.model.Review;
import com.recipehub.model.User;
import com.recipehub.repository.RecipeRepository;
import com.recipehub.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ReviewService manages business logic for recipe reviews, ratings,
 * and automatic average rating recalculation.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RecipeRepository recipeRepository;

    public ReviewService(ReviewRepository reviewRepository, RecipeRepository recipeRepository) {
        this.reviewRepository = reviewRepository;
        this.recipeRepository = recipeRepository;
    }

    /**
     * Adds a review (1-5 stars + optional comment) to a recipe.
     * Automatically updates the recipe's average rating in the database.
     */
    public Review addReview(Long recipeId, Review review, User user) {
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found with id: " + recipeId);
        }

        // Validate rating range
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        review.setRecipeId(recipeId);
        review.setUserId(user.getId());
        review.setUserName(user.getName());

        Review savedReview = reviewRepository.save(review);

        // Recalculate average rating for the recipe
        recalculateAverageRating(recipe);

        return savedReview;
    }

    /**
     * Retrieves all reviews submitted for a specific recipe.
     */
    public List<Review> getReviewsByRecipeId(Long recipeId) {
        return reviewRepository.findByRecipeId(recipeId);
    }

    /**
     * Deletes a review by ID.
     * Allowed only for the review author or an ADMIN.
     */
    public boolean deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null) {
            throw new IllegalArgumentException("Review not found with id: " + reviewId);
        }

        boolean isAuthor = review.getUserId() != null && review.getUserId().equals(user.getId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        if (!isAuthor && !isAdmin) {
            throw new IllegalArgumentException("You are not authorized to delete this review");
        }

        Long recipeId = review.getRecipeId();
        reviewRepository.delete(review);

        // Recalculate average rating after review deletion
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe != null) {
            recalculateAverageRating(recipe);
        }

        return true;
    }

    /**
     * Helper method to compute the average rating of a recipe from all its reviews
     * and persist it on the Recipe entity.
     */
    private void recalculateAverageRating(Recipe recipe) {
        List<Review> reviews = reviewRepository.findByRecipeId(recipe.getId());
        if (reviews.isEmpty()) {
            recipe.setAverageRating(0.0);
        } else {
            double total = 0.0;
            for (Review r : reviews) {
                total += r.getRating();
            }
            double average = total / reviews.size();
            // Round to 1 decimal place (e.g. 4.67 -> 4.7)
            double roundedAverage = Math.round(average * 10.0) / 10.0;
            recipe.setAverageRating(roundedAverage);
        }
        recipeRepository.save(recipe);
    }
}
