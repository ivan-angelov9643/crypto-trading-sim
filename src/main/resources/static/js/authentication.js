function toggleForm() {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("register-form");
    const formTitle = document.getElementById("form-title");

    if (loginForm.classList.contains("hidden")) {
        loginForm.classList.remove("hidden");
        registerForm.classList.add("hidden");
        formTitle.textContent = "Login";
    } else {
        loginForm.classList.add("hidden");
        registerForm.classList.remove("hidden");
        formTitle.textContent = "Register";
    }
}

async function login(event) {
    event.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    const response = await fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
    });

    if (response.ok) {
        const token = await response.text();
        console.log(token)
        localStorage.setItem('jwtToken', token);
        alert('Login successful!');
        window.location.href = 'cryptoView.html';
    } else {
        const errorMessage = await response.text();
        alert(`Login failed: ${errorMessage}`);
    }
}

async function register(event) {
    event.preventDefault();
    const username = document.getElementById('register-username').value;
    const password = document.getElementById('register-password').value;

    const response = await fetch('/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
    });

    if (response.ok) {
        alert('Registration successful!');
        window.location.href = 'index.html';
    } else {
        const errorMessage = await response.text();
        alert(`Registration failed: ${errorMessage}`);
    }
}

const loginForm = document.getElementById('login-form');
loginForm.addEventListener('submit', login);

const registerForm = document.getElementById('register-form');
registerForm.addEventListener('submit', register);
