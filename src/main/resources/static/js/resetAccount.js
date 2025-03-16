async function reset() {
    try {
        const token = localStorage.getItem("jwtToken");

        const response = await fetch("/reset", {
            method: "POST",
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
        });

        if (response.ok) {
            location.reload();
            console.log(await response.text());
            alert('Account reset successfully!');
        } else {
            const errorMessage = await response.text();
            alert(`Account reset failed: ${errorMessage}`);
        }
    } catch (error) {
        console.error('Error resetting account:', error);
        alert('An error occurred while resetting your account.');
    }
}

document.getElementById('reset-button').addEventListener('click', reset);