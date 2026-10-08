/**
 * admin.js - Admin moderation queue handler
 */

document.addEventListener('DOMContentLoaded', async () => {
    const user = await checkCurrentUser();
    if (!user) {
        window.location.href = 'login.html';
        return;
    }

    if (user.role !== 'ADMIN') {
        alert('Access Denied: Only administrators can view the moderation panel.');
        window.location.href = 'index.html';
        return;
    }

    loadPendingRecipes();
});

async function loadPendingRecipes() {
    const tbody = document.getElementById('pending-recipes-tbody');
    const tableContainer = document.getElementById('pending-table-container');
    const noPendingMsg = document.getElementById('no-pending-msg');

    if (!tbody) return;

    tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 30px; color: var(--text-muted);">Loading pending queue...</td></tr>';

    try {
        const res = await fetch('/api/admin/recipes/pending');
        if (!res.ok) {
            if (res.status === 403) {
                alert('Access denied: Admin role required.');
                window.location.href = 'index.html';
                return;
            }
            throw new Error('Failed to load pending recipes');
        }

        const pending = await res.json();

        if (pending.length === 0) {
            tableContainer.style.display = 'none';
            noPendingMsg.style.display = 'block';
            return;
        }

        tableContainer.style.display = 'block';
        noPendingMsg.style.display = 'none';

        tbody.innerHTML = pending.map(recipe => {
            const img = recipe.imageUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600';
            return `
                <tr>
                    <td>
                        <img src="${escapeHtml(img)}" alt="${escapeHtml(recipe.title)}" style="width: 60px; height: 60px; object-fit: cover; border-radius: 6px;" onerror="this.src='https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600'" />
                    </td>
                    <td>
                        <strong>${escapeHtml(recipe.title)}</strong>
                        <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 3px;">
                            <a href="recipe-details.html?id=${recipe.id}" target="_blank" style="color: var(--primary-orange);">Preview Details ↗</a>
                        </div>
                    </td>
                    <td><span class="card-category">${escapeHtml(recipe.category || 'General')}</span></td>
                    <td>${escapeHtml(recipe.authorName || 'Chef')}</td>
                    <td style="max-width: 250px; font-size: 0.85rem; color: var(--text-muted);">
                        ${escapeHtml(recipe.description || '')}
                    </td>
                    <td>
                        <div class="action-buttons">
                            <button onclick="handleApprove(${recipe.id})" class="btn-success" style="font-size: 0.82rem; padding: 6px 12px;">
                                ✓ Approve
                            </button>
                            <button onclick="handleReject(${recipe.id})" class="btn-danger" style="font-size: 0.82rem; padding: 6px 12px;">
                                ✕ Reject
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (err) {
        console.error('Error fetching pending recipes:', err);
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; padding: 30px; color: var(--status-rejected);">Failed to load moderation queue.</td></tr>';
    }
}

async function handleApprove(recipeId) {
    if (!confirm('Approve this recipe? It will become publicly visible on the recipes catalog.')) return;

    try {
        const res = await fetch(`/api/admin/recipes/${recipeId}/approve`, { method: 'PUT' });
        const data = await res.json();

        if (res.ok) {
            showAlert('admin-alert', 'Recipe approved successfully! It is now live.', 'success');
            await loadPendingRecipes();
        } else {
            showAlert('admin-alert', data.message || 'Error approving recipe.', 'error');
        }
    } catch (err) {
        console.error('Approval error:', err);
        showAlert('admin-alert', 'Network error during approval.', 'error');
    }
}

async function handleReject(recipeId) {
    if (!confirm('Reject this recipe? It will NOT appear on the public recipes catalog.')) return;

    try {
        const res = await fetch(`/api/admin/recipes/${recipeId}/reject`, { method: 'PUT' });
        const data = await res.json();

        if (res.ok) {
            showAlert('admin-alert', 'Recipe rejected.', 'success');
            await loadPendingRecipes();
        } else {
            showAlert('admin-alert', data.message || 'Error rejecting recipe.', 'error');
        }
    } catch (err) {
        console.error('Rejection error:', err);
        showAlert('admin-alert', 'Network error during rejection.', 'error');
    }
}
