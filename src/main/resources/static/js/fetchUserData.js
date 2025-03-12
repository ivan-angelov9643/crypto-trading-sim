window.userId = localStorage.getItem("userId");
if (!window.userId) {
    window.userId = crypto.randomUUID();
    localStorage.setItem("userId", window.userId);
}

async function fetchUserData() {
    try {
        const response = await fetch(`/users-data/${window.userId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch user account data');
        }
        const userData = await response.json();
        console.log("balance: " + userData.balance)
        document.getElementById('balance').textContent = userData.balance.toFixed(2);

        updateOwnedAssets(userData.ownedAssets);
        updateTransactionHistory(userData.transactions);
    } catch (error) {
        console.error('Error fetching user account:', error);
    }
}
// TODO optimize updating transactions and owned assets to update only the changed ones (backend too)
function updateOwnedAssets(ownedAssets) {
     for (const [asset, assetData] of Object.entries(ownedAssets)) {
         const quantityElement = document.getElementById(`${asset}-owned-quantity`);
         const averagePriceElement = document.getElementById(`${asset}-average-price`);

         if (quantityElement && averagePriceElement) {
             quantityElement.textContent = assetData.quantity.toFixed(6);
             averagePriceElement.textContent = `$${assetData.averagePrice.toFixed(2)}`;
         } else {
             console.error(`Elements for asset ${asset} not found in the DOM.`);
         }
     }
 }

function updateTransactionHistory(transactions) {
    const historyList = document.getElementById('history-list');
    historyList.innerHTML = "";

    transactions.forEach(transaction => {
        const listItem = document.createElement('li');

        if (transaction.type.toLowerCase() === 'buy') {
                action = 'Bought';
            } else if (transaction.type.toLowerCase() === 'sell') {
                action = 'Sold';
        }

        listItem.textContent = `${action} ${transaction.quantity} ${transaction.asset}
        at $${transaction.price} each for a total of $${transaction.value}`;

        historyList.appendChild(listItem);
    });
}

document.addEventListener('DOMContentLoaded', fetchUserData);