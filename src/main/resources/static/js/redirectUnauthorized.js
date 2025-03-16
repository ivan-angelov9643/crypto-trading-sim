document.addEventListener("DOMContentLoaded", async function () {
    const token = localStorage.getItem('jwtToken');
    console.log(token)
    if (!token) {
        window.location.href = "/";
        return;
    }

    try {
        const response = await fetch("/validate-token", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Invalid token");
        }

    } catch (error) {
        console.warn("Token validation failed:", error.message);
        localStorage.removeItem("jwtToken");
        window.location.href = "/";
    }
});