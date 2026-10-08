/**
 * recipes.js - Public recipe catalog browsing and searching
 */

let activeCategory = 'All';
let activeSearch = '';

document.addEventListener('DOMContentLoaded', () => {
    // Check URL parameters for search or category
    const urlParams = new URLSearchParams(window.location.search);
    const initialSearch = urlParams.get('search');
    const initialCategory = urlParams.get('category');

    if (initialSearch) {
        activeSearch = initialSearch;
        document.getElementById('recipe-search-input').value = initialSearch;
    }

    if (initialCategory) {
        activeCategory = initialCategory;
        updateCategoryButtonsUI(initialCategory);
    }

    loadCatalogRecipes();

    // Search form listener
    const searchForm = document.getElementById('recipes-search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            activeSearch = document.getElementById('recipe-search-input').value.trim();
            loadCatalogRecipes();
        });
    }

    // Clear search listener
    const clearBtn = document.getElementById('btn-clear-search');
    if (clearBtn) {
        clearBtn.addEventListener('click', () => {
            document.getElementById('recipe-search-input').value = '';
            activeSearch = '';
            loadCatalogRecipes();
        });
    }
});

async function loadCatalogRecipes() {
    const grid = document.getElementById('recipe-catalog-grid');
    if (!grid) return;

    grid.innerHTML = '<p style="text-align:center; color: var(--text-muted); grid-column: 1/-1;">Loading recipes...</p>';

    try {
        let url = '/api/recipes?';
        const params = [];

        if (activeSearch) {
            params.push(`search=${encodeURIComponent(activeSearch)}`);
        }
        if (activeCategory && activeCategory !== 'All') {
            params.push(`category=${encodeURIComponent(activeCategory)}`);
        }

        url += params.join('&');

        const res = await fetch(url);
        if (!res.ok) throw new Error('Failed to load recipes');
        const recipes = await res.json();

        if (recipes.length === 0) {
            grid.innerHTML = `
                <div style="text-align: center; grid-column: 1/-1; padding: 40px 0;">
                    <p style="font-size: 1.1rem; color: var(--text-muted);">No matching approved recipes found.</p>
                    <button class="btn-secondary" style="margin-top: 15px;" onclick="resetFilters()">View All Recipes</button>
                </div>
            `;
            return;
        }

        grid.innerHTML = recipes.map(recipe => renderRecipeCard(recipe)).join('');
    } catch (err) {
        console.error('Error fetching recipes:', err);
        grid.innerHTML = '<p style="text-align:center; color: var(--status-rejected); grid-column: 1/-1;">Error loading recipes.</p>';
    }
}

function setCategoryFilter(category) {
    activeCategory = category;
    updateCategoryButtonsUI(category);
    loadCatalogRecipes();
}

function updateCategoryButtonsUI(category) {
    document.querySelectorAll('.cat-pill').forEach(btn => {
        btn.classList.toggle('active', btn.textContent.trim().toLowerCase() === category.toLowerCase());
    });
}

function resetFilters() {
    activeSearch = '';
    activeCategory = 'All';
    document.getElementById('recipe-search-input').value = '';
    updateCategoryButtonsUI('All');
    loadCatalogRecipes();
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
