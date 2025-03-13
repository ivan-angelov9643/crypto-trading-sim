
const socket = new WebSocket('ws://localhost:8080/crypto-prices');

socket.onmessage = function(event) {
//    console.log('Received: ' + event.data);

    try {
        const data = JSON.parse(event.data);

        if (data.hasOwnProperty("name") && data.hasOwnProperty("symbol") && data.hasOwnProperty("price")) {
            updateSinglePrice(data);
        } else {
            updateAllPrices(data);
        }
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

function updateAllPrices(prices) {
    for (const [symbol, crypto] of Object.entries(prices)) {
        updateCryptoTable(crypto);
    }
}

function updateSinglePrice(crypto) {
    updateCryptoTable(crypto);
}

function updateCryptoTable(crypto) {
    const priceElement = document.getElementById(`${crypto.symbol}-price`);

    if (priceElement) {
        priceElement.textContent = `$${crypto.price}`;
    }
}