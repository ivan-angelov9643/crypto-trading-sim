
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
    for (const [symbol, crypto] of Object.entries(prices)) {
        const priceElement = document.getElementById(`${symbol}-price`);

        //TODO FETCH ONLY CHANGED PRICES, NOT ALL
        if (priceElement) {
            priceElement.textContent = `$${crypto.price}`;
        }
    }
}