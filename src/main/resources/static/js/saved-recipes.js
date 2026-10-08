/**
 * saved-recipes.js - User bookmarks list and removal
 */

document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkCurrentUser();
    if (!user) {
        window.location.href = 'login.html';
        return;
    }

    loadSavedRecipes();
});

async function loadSavedRecipes() {
    const grid = document.getElementById('saved-recipes-grid');
    if (!grid) return;

    grid.innerHTML = '<p style="text-align:center; color: var(--text-muted); grid-column: 1/-1;">Loading your saved recipes...</p>';

    try {
        const res = await fetch('/api/saved-recipes');
        if (!res.ok) throw new Error('Failed to load saved recipes');
        const recipes = await res.json();

        if (recipes.length === 0) {
            grid.innerHTML = `
                <div style="text-align:center; grid-column: 1/-1; padding: 40px 0;">
                    <p style="font-size: 1.1rem; color: var(--text-muted); margin-bottom: 15px;">You haven't saved any recipes yet.</p>
                    <a href="recipes.html" class="btn-primary">Explore & Save Recipes</a>
                </div>
            `;
            return;
        }

        grid.innerHTML = recipes.map(recipe => renderSavedRecipeCard(recipe)).join('');
    } catch (err) {
        console.error('Error fetching saved recipes:', err);
        grid.innerHTML = '<p style="text-align:center; color: var(--status-rejected); grid-column: 1/-1;">Error loading saved recipes.</p>';
    }
}

function renderSavedRecipeCard(recipe) {
    const imgUrl = recipe.imageUrl || window.DEFAULT_RECIPE_IMAGE;
    const ratingDisplay = recipe.averageRating && recipe.averageRating > 0
        ? `<span class="star">★</span> ${recipe.averageRating.toFixed(1)}`
        : `<span style="color: var(--text-muted); font-weight: normal;">No ratings</span>`;

    return `
        <div class="recipe-card">
            <img src="${escapeHtml(imgUrl)}" alt="${escapeHtml(recipe.title)}" class="recipe-card-img" onerror="handleImageError(this)" />
            <div class="recipe-card-body">
                <div class="recipe-meta-top">
                    <span class="card-category">${escapeHtml(recipe.category || 'General')}</span>
                    <div class="card-rating">${ratingDisplay}</div>
                </div>
                <a href="recipe-details.html?id=${recipe.id}" class="recipe-card-title">${escapeHtml(recipe.title)}</a>
                <p class="recipe-card-desc">${escapeHtml(recipe.description || '')}</p>
                <div class="recipe-card-footer">
                    <a href="recipe-details.html?id=${recipe.id}" class="btn-primary" style="padding: 6px 12px; font-size: 0.8rem;">View</a>
                    <button onclick="handleRemoveBookmark(${recipe.id})" class="btn-logout" style="padding: 6px 10px; font-size: 0.8rem;">
                        Remove ✕
                    </button>
                </div>
            </div>
        </div>
    `;
}

async function handleRemoveBookmark(recipeId) {
    try {
        const res = await fetch(`/api/recipes/${recipeId}/save`, { method: 'DELETE' });
        const data = await res.json();

        if (res.ok) {
            showAlert('saved-alert', 'Recipe removed from your saved collection.', 'success');
            await loadSavedRecipes();
        } else {
            showAlert('saved-alert', data.message || 'Error removing recipe.', 'error');
        }
    } catch (err) {
        console.error('Error removing bookmark:', err);
        showAlert('saved-alert', 'Network error removing recipe.', 'error');
    }
}
