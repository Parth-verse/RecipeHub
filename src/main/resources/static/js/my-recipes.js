/**
 * my-recipes.js - Contributor recipes management
 */

document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkCurrentUser();
    if (!user) {
        window.location.href = 'login.html';
        return;
    }

    loadMyRecipes();
});

async function loadMyRecipes() {
    const grid = document.getElementById('my-recipes-grid');
    if (!grid) return;

    grid.innerHTML = '<p style="text-align:center; color: var(--text-muted); grid-column: 1/-1;">Loading your recipes...</p>';

    try {
        const res = await fetch('/api/recipes/my');
        if (!res.ok) throw new Error('Failed to load recipes');
        const recipes = await res.json();

        if (recipes.length === 0) {
            grid.innerHTML = `
                <div style="text-align:center; grid-column: 1/-1; padding: 40px 0;">
                    <p style="font-size: 1.1rem; color: var(--text-muted); margin-bottom: 15px;">You haven't submitted any recipes yet.</p>
                    <a href="add-recipe.html" class="btn-primary">+ Create Your First Recipe</a>
                </div>
            `;
            return;
        }

        grid.innerHTML = recipes.map(recipe => renderMyRecipeCard(recipe)).join('');
    } catch (err) {
        console.error('Error fetching my recipes:', err);
        grid.innerHTML = '<p style="text-align:center; color: var(--status-rejected); grid-column: 1/-1;">Error loading your recipes.</p>';
    }
}

function renderMyRecipeCard(recipe) {
    const imgUrl = recipe.imageUrl || window.DEFAULT_RECIPE_IMAGE;
    const statusClass = recipe.status || 'PENDING';

    return `
        <div class="recipe-card">
            <img src="${escapeHtml(imgUrl)}" alt="${escapeHtml(recipe.title)}" class="recipe-card-img" onerror="handleImageError(this)" />
            <div class="recipe-card-body">
                <div class="recipe-meta-top">
                    <span class="card-category">${escapeHtml(recipe.category || 'General')}</span>
                    <span class="status-badge ${statusClass}">${statusClass}</span>
                </div>
                <h3 class="recipe-card-title">${escapeHtml(recipe.title)}</h3>
                <p class="recipe-card-desc">${escapeHtml(recipe.description || '')}</p>
                <div class="recipe-card-footer" style="padding-top: 10px;">
                    <a href="recipe-details.html?id=${recipe.id}" class="btn-secondary" style="font-size: 0.8rem; padding: 5px 10px;">View</a>
                    <div style="display: flex; gap: 6px;">
                        <a href="add-recipe.html?edit=${recipe.id}" class="btn-secondary" style="font-size: 0.8rem; padding: 5px 10px;">Edit</a>
                        <button onclick="handleDeleteRecipe(${recipe.id})" class="btn-danger" style="font-size: 0.8rem; padding: 5px 10px;">Delete</button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

async function handleDeleteRecipe(recipeId) {
    if (!confirm('Are you sure you want to delete this recipe? This action cannot be undone.')) return;

    try {
        const res = await fetch(`/api/recipes/${recipeId}`, { method: 'DELETE' });
        const data = await res.json();

        if (res.ok) {
            showAlert('my-recipes-alert', 'Recipe deleted successfully.', 'success');
            await loadMyRecipes();
        } else {
            showAlert('my-recipes-alert', data.message || 'Failed to delete recipe.', 'error');
        }
    } catch (err) {
        console.error('Delete error:', err);
        showAlert('my-recipes-alert', 'Network error while deleting recipe.', 'error');
    }
}
