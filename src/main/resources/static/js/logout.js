async function logout() {
    try {
        const token = localStorage.getItem("jwtToken");
        const response = await fetch("/logout", {
            method: "POST",
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });
        if (response.ok) {
            localStorage.removeItem("jwtToken");
            window.location.href = "/";
        } else {
            const errorMessage = await response.text();
            alert(`Logout failed: ${errorMessage}`);
        }
    } catch(error) {
        console.error('Error logging out:', error);
        return null;
    }
}

const logoutButton = document.getElementById("logout-button");
logoutButton.addEventListener("click", logout);
