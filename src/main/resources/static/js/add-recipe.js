/**
 * add-recipe.js - Create and edit recipes
 */

let editRecipeId = null;

document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkCurrentUser();
    if (!user) {
        window.location.href = 'login.html';
        return;
    }

    if (user.role !== 'CONTRIBUTOR' && user.role !== 'ADMIN') {
        alert('Only Contributors and Admins can create recipes.');
        window.location.href = 'recipes.html';
        return;
    }

    // Check if in edit mode
    const urlParams = new URLSearchParams(window.location.search);
    editRecipeId = urlParams.get('edit');

    if (editRecipeId) {
        setupEditMode();
    }

    const form = document.getElementById('recipe-form');
    if (form) {
        form.addEventListener('submit', handleRecipeFormSubmit);
    }
});

async function setupEditMode() {
    document.getElementById('page-heading').textContent = 'Edit Recipe';
    document.getElementById('page-subtitle').textContent = 'Make changes to your recipe. Modified recipes will be re-submitted for admin review.';
    document.getElementById('btn-submit-recipe').textContent = 'Save Changes';
    document.title = 'Edit Recipe — RecipeHub';

    try {
        const res = await fetch(`/api/recipes/${editRecipeId}`);
        if (!res.ok) throw new Error('Recipe not found');
        const recipe = await res.json();

        document.getElementById('recipe-title').value = recipe.title || '';
        document.getElementById('recipe-category').value = recipe.category || 'Curry';
        document.getElementById('recipe-image-url').value = recipe.imageUrl || '';
        document.getElementById('recipe-description').value = recipe.description || '';
        document.getElementById('recipe-ingredients').value = recipe.ingredients || '';
        document.getElementById('recipe-instructions').value = recipe.instructions || '';
    } catch (err) {
        console.error('Error loading recipe for edit:', err);
        showAlert('recipe-form-alert', 'Error loading recipe for editing', 'error');
    }
}

async function handleRecipeFormSubmit(e) {
    e.preventDefault();

    const title = document.getElementById('recipe-title').value.trim();
    const category = document.getElementById('recipe-category').value;
    const imageUrl = document.getElementById('recipe-image-url').value.trim();
    const description = document.getElementById('recipe-description').value.trim();
    const ingredients = document.getElementById('recipe-ingredients').value.trim();
    const instructions = document.getElementById('recipe-instructions').value.trim();

    if (!title || !description || !ingredients || !instructions) {
        showAlert('recipe-form-alert', 'Please fill in all required fields.', 'error');
        return;
    }

    const payload = {
        title,
        category,
        imageUrl: imageUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600',
        description,
        ingredients,
        instructions
    };

    try {
        let url = '/api/recipes';
        let method = 'POST';

        if (editRecipeId) {
            url = `/api/recipes/${editRecipeId}`;
            method = 'PUT';
        }

        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await res.json();

        if (res.ok) {
            showAlert('recipe-form-alert', data.message || 'Recipe saved successfully!', 'success');
            setTimeout(() => {
                window.location.href = 'my-recipes.html';
            }, 1200);
        } else {
            showAlert('recipe-form-alert', data.message || 'Failed to save recipe.', 'error');
        }
    } catch (err) {
        console.error('Recipe save error:', err);
        showAlert('recipe-form-alert', 'Network error. Please try again.', 'error');
    }
}
