document.getElementById('reset-button').addEventListener('click', resetAccount);

function resetAccount() {
    localStorage.removeItem("userId");
    console.log('Account reset. Reloading page...');
    location.reload();
}

