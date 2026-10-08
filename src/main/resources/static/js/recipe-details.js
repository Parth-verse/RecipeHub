/**
 * recipe-details.js - Handles detailed recipe display, bookmarking, and reviews
 */

let currentRecipeId = null;
let isCurrentlySaved = false;

document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    currentRecipeId = urlParams.get('id');

    if (!currentRecipeId) {
        window.location.href = 'recipes.html';
        return;
    }

    await loadRecipeDetails();
    await checkSavedStatus();
    await loadReviews();

    // Check login state to toggle review form
    const user = await checkCurrentUser();
    const addReviewSec = document.getElementById('add-review-section');
    const loginPrompt = document.getElementById('login-to-review-msg');

    if (!user) {
        if (addReviewSec) addReviewSec.style.display = 'none';
        if (loginPrompt) loginPrompt.style.display = 'block';
    } else {
        if (addReviewSec) addReviewSec.style.display = 'block';
        if (loginPrompt) loginPrompt.style.display = 'none';
    }

    // Attach review form listener
    const reviewForm = document.getElementById('review-form');
    if (reviewForm) {
        reviewForm.addEventListener('submit', handleReviewSubmit);
    }
});

async function loadRecipeDetails() {
    try {
        const res = await fetch(`/api/recipes/${currentRecipeId}`);
        if (!res.ok) {
            document.getElementById('details-loading').innerHTML = '<p style="color: var(--status-rejected);">Recipe not found.</p>';
            return;
        }

        const recipe = await res.json();

        document.getElementById('details-loading').style.display = 'none';
        document.getElementById('recipe-details-container').style.display = 'block';

        document.getElementById('detail-title').textContent = recipe.title;
        document.title = `${recipe.title} — RecipeHub`;
        document.getElementById('detail-author').textContent = recipe.authorName || 'Chef';
        document.getElementById('detail-category').textContent = recipe.category || 'General';
        document.getElementById('detail-description').textContent = recipe.description || '';

        const ratingVal = recipe.averageRating && recipe.averageRating > 0 ? recipe.averageRating.toFixed(1) : 'No ratings';
        document.getElementById('detail-rating').textContent = ratingVal;

        const img = document.getElementById('detail-img');
        img.src = recipe.imageUrl || window.DEFAULT_RECIPE_IMAGE;
        img.alt = recipe.title;
        img.onerror = () => handleImageError(img);

        // Render ingredients
        const ingBox = document.getElementById('detail-ingredients');
        const ingredients = recipe.ingredients || 'No ingredients listed.';
        ingBox.innerHTML = `<pre style="white-space: pre-line; font-family: inherit;">${escapeHtml(ingredients)}</pre>`;

        // Render instructions
        const instBox = document.getElementById('detail-instructions');
        const instructions = recipe.instructions || 'No instructions listed.';
        instBox.innerHTML = `<pre style="white-space: pre-line; font-family: inherit;">${escapeHtml(instructions)}</pre>`;

    } catch (err) {
        console.error('Error loading recipe:', err);
        document.getElementById('details-loading').innerHTML = '<p style="color: var(--status-rejected);">Error loading recipe details.</p>';
    }
}

async function checkSavedStatus() {
    const saveBtn = document.getElementById('btn-save-recipe');
    if (!saveBtn) return;

    try {
        const res = await fetch(`/api/recipes/${currentRecipeId}/is-saved`);
        if (res.ok) {
            const data = await res.json();
            isCurrentlySaved = data.isSaved;
            updateSaveButtonUI();
        }
    } catch (err) {
        console.error('Error checking saved status:', err);
    }
}

function updateSaveButtonUI() {
    const saveBtn = document.getElementById('btn-save-recipe');
    if (!saveBtn) return;

    if (isCurrentlySaved) {
        saveBtn.textContent = '✓ Saved in Collection';
        saveBtn.className = 'btn-secondary';
        saveBtn.style.color = 'var(--status-approved)';
        saveBtn.style.borderColor = 'var(--status-approved)';
    } else {
        saveBtn.textContent = '🔖 Save Recipe';
        saveBtn.className = 'btn-primary';
        saveBtn.style.color = 'white';
        saveBtn.style.borderColor = '';
    }
}

async function toggleSaveRecipe() {
    const user = await checkCurrentUser();
    if (!user) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const method = isCurrentlySaved ? 'DELETE' : 'POST';
        const res = await fetch(`/api/recipes/${currentRecipeId}/save`, { method });
        const data = await res.json();

        if (res.ok) {
            isCurrentlySaved = !isCurrentlySaved;
            updateSaveButtonUI();
        } else {
            alert(data.message || 'Error updating bookmark');
        }
    } catch (err) {
        console.error('Error saving recipe:', err);
    }
}

async function loadReviews() {
    const reviewsList = document.getElementById('reviews-list');
    if (!reviewsList) return;

    try {
        const res = await fetch(`/api/recipes/${currentRecipeId}/reviews`);
        if (!res.ok) throw new Error('Failed to load reviews');
        const reviews = await res.json();

        if (reviews.length === 0) {
            reviewsList.innerHTML = '<p style="color: var(--text-muted); font-size: 0.95rem;">No reviews yet. Be the first to share your thoughts!</p>';
            return;
        }

        reviewsList.innerHTML = reviews.map(r => {
            const stars = '★'.repeat(r.rating) + '☆'.repeat(5 - r.rating);
            const canDelete = currentUser && (currentUser.id === r.userId || currentUser.role === 'ADMIN');
            const deleteBtn = canDelete ? `
                <button onclick="handleDeleteReview(${r.id})" class="btn-logout" style="font-size: 0.75rem; padding: 3px 8px;">Delete</button>
            ` : '';

            return `
                <div class="review-item">
                    <div class="review-header">
                        <div>
                            <span class="reviewer-name">${escapeHtml(r.userName || 'Foodie')}</span>
                            <span class="review-stars" style="margin-left: 8px;">${stars}</span>
                        </div>
                        ${deleteBtn}
                    </div>
                    <p style="color: var(--text-dark); font-size: 0.92rem; margin-top: 4px;">
                        ${r.comment ? escapeHtml(r.comment) : '<em>No comment left.</em>'}
                    </p>
                </div>
            `;
        }).join('');
    } catch (err) {
        console.error('Error loading reviews:', err);
    }
}

async function handleReviewSubmit(e) {
    e.preventDefault();
    const rating = parseInt(document.getElementById('review-rating').value, 10);
    const comment = document.getElementById('review-comment').value.trim();

    try {
        const res = await fetch(`/api/recipes/${currentRecipeId}/reviews`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ rating, comment })
        });

        const data = await res.json();

        if (res.ok) {
            showAlert('review-alert', 'Review submitted successfully!', 'success');
            document.getElementById('review-comment').value = '';
            await loadReviews();
            await loadRecipeDetails(); // reload average rating
        } else {
            showAlert('review-alert', data.message || 'Failed to submit review', 'error');
        }
    } catch (err) {
        console.error('Review submit error:', err);
        showAlert('review-alert', 'Network error. Please try again.', 'error');
    }
}

async function handleDeleteReview(reviewId) {
    if (!confirm('Are you sure you want to delete this review?')) return;

    try {
        const res = await fetch(`/api/reviews/${reviewId}`, { method: 'DELETE' });
        if (res.ok) {
            await loadReviews();
            await loadRecipeDetails();
        } else {
            const data = await res.json();
            alert(data.message || 'Failed to delete review');
        }
    } catch (err) {
        console.error('Error deleting review:', err);
    }
}
