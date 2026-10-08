/**
 * index.js - Homepage recipe loader and category filter
 */

document.addEventListener('DOMContentLoaded', () => {
    loadFeaturedRecipes();

    const searchForm = document.getElementById('hero-search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const query = document.getElementById('hero-search-input').value.trim();
            if (query) {
                window.location.href = `recipes.html?search=${encodeURIComponent(query)}`;
            }
        });
    }
});

async function loadFeaturedRecipes(category = '') {
    const grid = document.getElementById('featured-recipes-grid');
    if (!grid) return;

    grid.innerHTML = '<p style="text-align:center; color: var(--text-muted); grid-column: 1/-1;">Loading recipes...</p>';

    try {
        let url = '/api/recipes';
        if (category && category !== 'All') {
            url += `?category=${encodeURIComponent(category)}`;
        }

        const res = await fetch(url);
        if (!res.ok) throw new Error('Failed to load recipes');
        const recipes = await res.json();

        if (recipes.length === 0) {
            grid.innerHTML = '<p style="text-align:center; color: var(--text-muted); grid-column: 1/-1;">No recipes found in this category.</p>';
            return;
        }

        grid.innerHTML = recipes.map(recipe => renderRecipeCard(recipe)).join('');
    } catch (err) {
        console.error('Error fetching recipes:', err);
        grid.innerHTML = '<p style="text-align:center; color: var(--status-rejected); grid-column: 1/-1;">Failed to load recipes.</p>';
    }
}

function filterHomeCategory(category) {
    document.querySelectorAll('.cat-pill').forEach(btn => {
        btn.classList.toggle('active', btn.textContent.trim().toLowerCase() === category.toLowerCase());
    });

    const heading = document.getElementById('grid-heading');
    if (heading) {
        heading.textContent = category === 'All' ? 'Featured Community Recipes' : `${category} Recipes`;
    }

    loadFeaturedRecipes(category);
}

function renderRecipeCard(recipe) {
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
                <p class="recipe-card-desc">${escapeHtml(recipe.description || 'Delicious home-cooked recipe.')}</p>
                <div class="recipe-card-footer">
                    <span>By ${escapeHtml(recipe.authorName || 'Chef')}</span>
                    <a href="recipe-details.html?id=${recipe.id}" class="btn-primary" style="padding: 6px 14px; font-size: 0.82rem;">View Recipe</a>
                </div>
            </div>
        </div>
    `;
}
