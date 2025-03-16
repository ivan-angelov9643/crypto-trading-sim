async function fetchUserData() {
    try {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`/user`, {
        method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });
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
        if (!isNaN(assetData.quantity)) {
            quantityElement.textContent = assetData.quantity.toFixed(6);
        } else {
            console.warn(`Invalid quantity for asset ${asset}:`, assetData.quantity);
        }

        if (!isNaN(assetData.averagePrice)) {
            averagePriceElement.textContent = `$${assetData.averagePrice.toFixed(2)}`;
        } else {
            console.warn(`Invalid average price for asset ${asset}:`, assetData.averagePrice);
        }
     } else {
         console.error(`Elements for asset ${asset} not found in the DOM.`);
     }
}

function updateAssets(ownedAssets) {
    if (!Array.isArray(ownedAssets)) {
        console.error('Owned assets should be an array');
        return;
    }

    for (let i = 0; i < ownedAssets.length; i++) {
        const assetData = ownedAssets[i];

        if (!assetData || !assetData.assetSymbol) {
            console.warn('Invalid asset data at index', i, ':', assetData);
            continue;
        }

        updateAsset(assetData.assetSymbol, assetData);
        updateVisibility(assetData.assetSymbol);
        updateProfit(assetData.assetSymbol);
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
        <td>${transaction.assetSymbol}</td>
        <td>${transaction.quantity}</td>
        <td>$${transaction.price}</td>
        <td>$${transaction.total}</td>
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

function extractUsername(token) {
    try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        return payload.sub;
    } catch (error) {
        console.error("Invalid token", error);
        return null;
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
        const token = localStorage.getItem('jwtToken');
        document.getElementById("username-display").textContent = `User: ${extractUsername(token)}`;
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
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`/assets/${asset}`, {
        method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });
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
        const token = localStorage.getItem('jwtToken');
        const response = await fetch(`/balance`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
            },
        });
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

document.addEventListener('DOMContentLoaded', fetchAndUpdateUserData);