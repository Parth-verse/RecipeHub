/**
 * login.js - Authentication handler
 */

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLoginSubmit);
    }
});

async function handleLoginSubmit(e) {
    e.preventDefault();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;

    if (!email || !password) {
        showAlert('login-alert', 'Please enter both email and password.', 'error');
        return;
    }

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (response.ok) {
            showAlert('login-alert', 'Login successful! Redirecting...', 'success');
            setTimeout(() => {
                if (data.role === 'ADMIN') {
                    window.location.href = 'admin.html';
                } else if (data.role === 'CONTRIBUTOR') {
                    window.location.href = 'my-recipes.html';
                } else {
                    window.location.href = 'recipes.html';
                }
            }, 800);
        } else {
            showAlert('login-alert', data.message || 'Invalid email or password', 'error');
        }
    } catch (err) {
        console.error('Login error:', err);
        showAlert('login-alert', 'Network error. Please try again.', 'error');
    }
}

function fillDemo(email, pass) {
    document.getElementById('email').value = email;
    document.getElementById('password').value = pass;
}
