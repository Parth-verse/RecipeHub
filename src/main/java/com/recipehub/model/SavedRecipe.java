package com.recipehub.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * SavedRecipe Entity representing a bookmarked recipe by a user.
 * Mapped to the 'saved_recipes' table in MySQL.
 */
@Entity
@Table(name = "saved_recipes")
public class SavedRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID of the user who saved the recipe
    @Column(nullable = false)
    private Long userId;

    // ID of the saved recipe
    @Column(nullable = false)
    private Long recipeId;

    // Default no-arg constructor required by JPA
    public SavedRecipe() {
    }

    // Parameterized constructor
    public SavedRecipe(Long userId, Long recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
