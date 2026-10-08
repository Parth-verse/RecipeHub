/**
 * common.js - Shared utilities, dynamic navigation bar, and image fallback for RecipeHub
 */

// Global variable to store active user
let currentUser = null;

// Standalone SVG Food Placeholder (zero external dependencies, renders cleanly offline)
window.DEFAULT_RECIPE_IMAGE = "data:image/svg+xml;charset=UTF-8,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22800%22%20height%3D%22500%22%20viewBox%3D%220%200%20800%20500%22%3E%3Crect%20fill%3D%22%23FFF3E0%22%20width%3D%22800%22%20height%3D%22500%22%2F%3E%3Ctext%20fill%3D%22%23E65100%22%20font-family%3D%22sans-serif%22%20font-size%3D%2260%22%20font-weight%3D%22bold%22%20x%3D%2250%25%22%20y%3D%2246%25%22%20text-anchor%3D%22middle%22%3E%F0%9F%8D%98%20RecipeHub%3C%2Ftext%3E%3Ctext%20fill%3D%22%23726A64%22%20font-family%3D%22sans-serif%22%20font-size%3D%2224%22%20x%3D%2250%25%22%20y%3D%2258%25%22%20text-anchor%3D%22middle%22%3EDelicious%20Dish%3C%2Ftext%3E%3C%2Fsvg%3E";

/**
 * Handles broken or missing image URLs smoothly without broken image icons
 */
window.handleImageError = function(img) {
    if (img) {
        img.onerror = null; // prevents infinite loop
        img.src = window.DEFAULT_RECIPE_IMAGE;
    }
};

// Initialize common navbar on DOMContentLoaded
document.addEventListener('DOMContentLoaded', async () => {
    await checkCurrentUser();
    renderNavbar();
});

/**
 * Checks session authentication via GET /api/auth/current
 */
async function checkCurrentUser() {
    try {
        const response = await fetch('/api/auth/current');
        if (response.ok) {
            currentUser = await response.json();
        } else {
            currentUser = null;
        }
    } catch (error) {
        console.error('Session check failed:', error);
        currentUser = null;
    }
    return currentUser;
}

/**
 * Renders the top navigation bar dynamically based on authentication & role
 */
function renderNavbar() {
    const navPlaceholder = document.getElementById('navbar-placeholder');
    if (!navPlaceholder) return;

    const currentPath = window.location.pathname.split('/').pop() || 'index.html';

    let navLinksHtml = `
        <li><a href="index.html" class="${currentPath === 'index.html' || currentPath === '' ? 'active' : ''}">Home</a></li>
        <li><a href="recipes.html" class="${currentPath === 'recipes.html' ? 'active' : ''}">Explore Recipes</a></li>
    `;

    if (!currentUser) {
        // Guest links
        navLinksHtml += `
            <li><a href="login.html" class="${currentPath === 'login.html' ? 'active' : ''}">Login</a></li>
            <li><a href="register.html" class="${currentPath === 'register.html' ? 'active' : ''} btn-primary" style="padding: 8px 16px; color: white;">Register</a></li>
        `;
    } else {
        // Authenticated links
        if (currentUser.role === 'CONTRIBUTOR' || currentUser.role === 'ADMIN') {
            navLinksHtml += `
                <li><a href="add-recipe.html" class="${currentPath === 'add-recipe.html' ? 'active' : ''}">+ Add Recipe</a></li>
                <li><a href="my-recipes.html" class="${currentPath === 'my-recipes.html' ? 'active' : ''}">My Recipes</a></li>
            `;
        }

        // Saved recipes available to all logged-in users
        navLinksHtml += `
            <li><a href="saved-recipes.html" class="${currentPath === 'saved-recipes.html' ? 'active' : ''}">Saved Recipes</a></li>
        `;

        if (currentUser.role === 'ADMIN') {
            navLinksHtml += `
                <li><a href="admin.html" class="${currentPath === 'admin.html' ? 'active' : ''}" style="color: var(--primary-orange); font-weight: 700;">Admin Panel</a></li>
            `;
        }

        // User badge & logout button
        navLinksHtml += `
            <li>
                <span class="user-badge">
                    ${escapeHtml(currentUser.name)}
                    <span class="role-pill">${escapeHtml(currentUser.role)}</span>
                </span>
            </li>
            <li>
                <button onclick="handleLogout()" class="btn-logout">Logout</button>
            </li>
        `;
    }

    navPlaceholder.innerHTML = `
        <nav class="navbar">
            <div class="nav-container">
                <a href="index.html" class="nav-brand">
                    <span class="nav-logo">🥘 RecipeHub</span>
                    <span class="nav-tagline">Discover. Create. Share.</span>
                </a>
                <ul class="nav-links">
                    ${navLinksHtml}
                </ul>
            </div>
        </nav>
    `;
}

/**
 * Handles user logout
 */
async function handleLogout() {
    try {
        const response = await fetch('/api/auth/logout', { method: 'POST' });
        if (response.ok) {
            window.location.href = 'login.html';
        }
    } catch (error) {
        console.error('Logout error:', error);
        window.location.href = 'login.html';
    }
}

/**
 * Helper to display alert messages
 */
function showAlert(elementId, message, type = 'error') {
    const alertBox = document.getElementById(elementId);
    if (!alertBox) return;

    alertBox.textContent = message;
    alertBox.className = `alert alert-${type}`;
    alertBox.style.display = 'block';

    setTimeout(() => {
        alertBox.style.display = 'none';
    }, 4500);
}

/**
 * Escapes HTML characters to prevent XSS in user text
 */
function escapeHtml(text) {
    if (!text) return '';
    return String(text)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
