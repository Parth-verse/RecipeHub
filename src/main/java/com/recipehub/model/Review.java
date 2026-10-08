package com.recipehub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Review Entity representing a rating (1-5) and user comment on a recipe.
 * Mapped to the 'reviews' table in the database.
 */
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Rating given by user: between 1 and 5
    @Column(nullable = false)
    private Integer rating;

    // User's written comment
    @Column(columnDefinition = "TEXT")
    private String comment;

    // ID of the user who wrote the review
    @Column(nullable = false)
    private Long userId;

    // ID of the recipe being reviewed
    @Column(nullable = false)
    private Long recipeId;

    // Reviewer's display name
    private String userName;

    public Review() {
    }

    public Review(Integer rating, String comment, Long userId, Long recipeId, String userName) {
        this.rating = rating;
        this.comment = comment;
        this.userId = userId;
        this.recipeId = recipeId;
        this.userName = userName;
    }

    // Standard Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
