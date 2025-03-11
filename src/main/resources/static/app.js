const socket = new WebSocket('ws://localhost:8080/crypto-prices');

socket.onmessage = function(event) {
//    console.log('Received: ' + event.data);

    try {
        const message = JSON.parse(event.data);
//        console.log('Parsed message:', message);

        updateCryptoTable(message);
    } catch (error) {
        console.error('Failed to parse WebSocket message:', error);
    }
};

socket.onopen = function() {
    console.log('WebSocket connection established');
};

socket.onclose = function() {
    console.log('WebSocket connection closed');
};

socket.onerror = function(error) {
    console.log('WebSocket error: ' + error);
};

function updateCryptoTable(prices) {
    const tableBody = document.querySelector('#crypto-table tbody');

    tableBody.innerHTML = '';

    for (const [symbol, crypto] of Object.entries(prices)) {
        const row = document.createElement('tr');

        const nameCell = document.createElement('td');
        nameCell.textContent = crypto.name;
        row.appendChild(nameCell);

        const symbolCell = document.createElement('td');
        symbolCell.textContent = crypto.symbol;
        row.appendChild(symbolCell);

        const priceCell = document.createElement('td');
        priceCell.textContent = `$${crypto.price}`;
        row.appendChild(priceCell);

        tableBody.appendChild(row);
    }
}

document.getElementById('buy-button').addEventListener('click', () => handleTransaction('buy'));
document.getElementById('sell-button').addEventListener('click', () => handleTransaction('sell'));
document.getElementById('reset-button').addEventListener('click', resetAccount);

function handleTransaction(type) {
    console.log(`${type} transaction initiated`);
}

function resetAccount() {
    console.log('Account reset');
}