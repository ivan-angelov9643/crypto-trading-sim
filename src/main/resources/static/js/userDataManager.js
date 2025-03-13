window.userId = localStorage.getItem("userId");
if (!window.userId) {
    window.userId = crypto.randomUUID();
    localStorage.setItem("userId", window.userId);
}

function resetAccount() {
    localStorage.removeItem("userId");
    console.log('Account reset. Reloading page...');
    location.reload();
}

async function fetchUserData() {
    return await fetch(`/users-data/${window.userId}`);
}

function updateAsset(asset, assetData) {
     const quantityElement = document.getElementById(`${asset}-owned-quantity`);
     const averagePriceElement = document.getElementById(`${asset}-average-price`);

     if (quantityElement && averagePriceElement) {
         quantityElement.textContent = assetData.quantity.toFixed(6);
         averagePriceElement.textContent = `$${assetData.averagePrice.toFixed(2)}`;
     } else {
         console.error(`Elements for asset ${asset} not found in the DOM.`);
     }
}

function updateAssets(ownedAssets) {
    for (const [asset, assetData] of Object.entries(ownedAssets)) {
        updateAsset(asset, assetData);
    }
}

function addTransactionToHistory(transaction) {
    const historyList = document.getElementById('history-list');

    const listItem = document.createElement('li');
    let action = '';

    if (transaction.type.toLowerCase() === 'buy') {
        action = 'Bought';
    } else if (transaction.type.toLowerCase() === 'sell') {
        action = 'Sold';
    }

    listItem.textContent = `${action} ${transaction.quantity} ${transaction.asset}
        at $${transaction.price} each for a total of $${transaction.value}`;

    historyList.appendChild(listItem);
}

function updateTransactionHistory(transactions) {
    const historyList = document.getElementById('history-list');
    historyList.innerHTML = "";

    for (let i = 0; i < transactions.length; i++) {
        addTransactionToHistory(transactions[i]);
    }
}

async function fetchAndUpdateUserData() {
    const response = await fetchUserData();
    if (!response.ok) {
        console.error('Failed to fetch user account data');
        return;
    }

    try {
        const userData = await response.json();

        document.getElementById('balance').textContent = userData.balance.toFixed(2);
        updateAssets(userData.ownedAssets);
        updateTransactionHistory(userData.transactions);
    } catch (error) {
        console.error('Error parsing user data:', error);
    }
}

async function fetchUserAsset(asset) {
    return await fetch(`/users-data/${window.userId}/assets/${asset}`);
}

async function fetchAndUpdateUserAsset(asset) {
    const response = await fetchUserAsset(asset);
    if (!response.ok) {
        console.error('Failed to fetch user asset');
        return;
    }

    try {
        const assetData = await response.json();

        updateAsset(asset, assetData);
    } catch (error) {
        console.error('Error parsing asset data:', error);
    }
}

async function fetchUserBalance() {
    return await fetch(`/users-data/${window.userId}/balance`);
}

async function fetchAndUpdateUserBalance() {
    const response = await fetchUserBalance();
    if (!response.ok) {
        console.error('Failed to fetch user balance');
        return;
    }

    try {
        const balanceData = await response.json();

        document.getElementById('balance').textContent = balanceData.toFixed(2);
    } catch (error) {
        console.error('Error parsing balance data:', error);
    }
}

document.getElementById('reset-button').addEventListener('click', resetAccount);
document.addEventListener('DOMContentLoaded', fetchAndUpdateUserData);