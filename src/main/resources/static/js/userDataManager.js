window.userId = localStorage.getItem("userId");
if (!window.userId) {
    window.userId = crypto.randomUUID();
    localStorage.setItem("userId", window.userId);
}

function resetAccount() {
    localStorage.removeItem("userId");
    console.info('Account reset. Reloading page...');
    location.reload();
}

async function fetchUserData() {
    try {
        const response = await fetch(`/users-data/${window.userId}`);
        if (!response.ok) {
            console.error('Failed to fetch user data. Status:', response.status);
            return null;
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching user data:', error);
        return null;
    }
}

function updateAsset(asset, assetData) {
    if (asset == null) {
        console.error('Asset is null or undefined');
        return;
    }
    if (assetData == null) {
        console.error('Asset data is null or undefined');
        return;
    }

     const quantityElement = document.getElementById(`${asset}-owned-quantity`);
     const averagePriceElement = document.getElementById(`${asset}-average-price`);

     if (quantityElement && averagePriceElement) {
        if (assetData.quantity && !isNaN(assetData.quantity)) {
            quantityElement.textContent = assetData.quantity.toFixed(6);
        } else {
            console.warn(`Invalid quantity for asset ${asset}:`, assetData.quantity);
        }

        if (assetData.averagePrice && !isNaN(assetData.averagePrice)) {
            averagePriceElement.textContent = `$${assetData.averagePrice.toFixed(2)}`;
        } else {
            console.warn(`Invalid average price for asset ${asset}:`, assetData.averagePrice);
        }
     } else {
         console.error(`Elements for asset ${asset} not found in the DOM.`);
     }
}

function updateAssets(ownedAssets) {
    if (ownedAssets == null) {
        console.error('Owned assets is null or undefined');
        return;
    }

    for (const [asset, assetData] of Object.entries(ownedAssets)) {
        updateAsset(asset, assetData);
        updateVisibility(asset);
        updateProfit(asset)
    }
}

function addTransactionToHistory(transaction) {
    if (transaction == null) {
        console.error('Transaction is null or undefined');
        return;
    }

    const historyTableBody = document.getElementById('history-table-body');

    const row = document.createElement('tr');
    let action = transaction.type.toLowerCase() === 'buy' ? 'Bought' : 'Sold';

    row.innerHTML = `
        <td>${action}</td>
        <td>${transaction.asset}</td>
        <td>${transaction.quantity}</td>
        <td>$${transaction.price}</td>
        <td>$${transaction.value}</td>
    `;

    historyTableBody.appendChild(row);

    console.info('Transaction added to history:', transaction);
}

function updateTransactionHistory(transactions) {
    if (transactions == null) {
        console.error('Transaction is null or undefined');
        return;
    }

    const historyList = document.getElementById('history-table-body');
    historyList.innerHTML = "";

    for (let i = 0; i < transactions.length; i++) {
        addTransactionToHistory(transactions[i]);
    }
}

async function fetchAndUpdateUserData() {
    const userData = await fetchUserData();
    if (!userData) {
        console.error('Failed to fetch or parse user account data');
        return;
    }

    try {
        if (userData.balance && !isNaN(userData.balance)) {
            document.getElementById('balance').textContent = userData.balance.toFixed(2);
        } else {
            console.warn('Invalid balance data:', userData.balance);
        }

        updateAssets(userData.ownedAssets);
        updateTransactionHistory(userData.transactions);
    } catch (error) {
        console.error('Error processing user data:', error);
    }
}

async function fetchUserAsset(asset) {
    if (asset == null) {
        console.error('Asset is null or undefined');
        return;
    }

    try {
        const response = await fetch(`/users-data/${window.userId}/assets/${asset}`);
        if (!response.ok) {
            console.error('Failed to fetch user asset:', asset, 'Status:', response.status);
            return null;
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching asset data:', asset, error);
        return null;
    }
}

async function fetchAndUpdateUserAsset(asset) {
    if (asset == null) {
        console.error('Asset is null or undefined');
        return;
    }

    const assetData = await fetchUserAsset(asset);
    if (!assetData) {
        console.error('Failed to fetch or parse asset data for', asset);
        return;
    }

    updateAsset(asset, assetData);
}

async function fetchUserBalance() {
    try {
        const response = await fetch(`/users-data/${window.userId}/balance`);
        if (!response.ok) {
            console.error('Failed to fetch user balance. Status:', response.status);
            return null;
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching balance data:', error);
        return null;
    }
}

async function fetchAndUpdateUserBalance() {
    const balanceData = await fetchUserBalance();
    if (!balanceData) {
        console.error('Failed to fetch or parse balance data');
        return;
    }

    if (!isNaN(balanceData)) {
        document.getElementById('balance').textContent = balanceData.toFixed(2);
    } else {
        console.warn('Invalid balance data:', balanceData);
    }
}

document.getElementById('reset-button').addEventListener('click', resetAccount);
document.addEventListener('DOMContentLoaded', fetchAndUpdateUserData);