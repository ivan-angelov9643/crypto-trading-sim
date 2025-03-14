function updatePrice(crypto) {
    if (crypto == null) {
        console.error('Crypto is null or undefined');
        return;
    }

    const { symbol, price } = crypto;

    if (symbol == null) {
        console.error('Symbol is null or undefined');
        return;
    }

    if (price == null) {
        console.error('Price is null or undefined');
        return;
    }

    const priceElement = document.getElementById(`${symbol}-price`);

    if (priceElement) {
        priceElement.textContent = `$${price}`;
    } else {
        console.warn(`Element not found for symbol: ${symbol}`);
    }

    console.debug(`Updated price for ${symbol}: $${price}`);
    updateProfit(symbol);
}

function updatePrices(prices) {
    if (prices == null) {
        console.error('Prices is null or undefined');
        return;
    }

    for (const [symbol, crypto] of Object.entries(prices)) {
        updatePrice(crypto);
    }
}

let socket;
function connectWebSocket() {
    if (socket && socket.readyState !== WebSocket.CLOSED) {
        socket.close();
    }

    socket = new WebSocket('ws://localhost:8080/crypto-prices');

    socket.onopen = function () {
        console.log('WebSocket connection established');
    };

    socket.onmessage = function (event) {
        try {
            const data = JSON.parse(event.data);

            if (!data) {
                console.error('Received empty WebSocket message');
                return;
            }

            if (data.symbol && typeof data.symbol === 'string' && data.hasOwnProperty("price")) {
                updatePrice(data);
            } else {
                updatePrices(data);
            }
        } catch (error) {
            console.error('Failed to parse WebSocket message:', error, 'Raw message:', event.data);
        }
    };

    socket.onclose = function (event) {
        console.warn(`WebSocket closed (code: ${event.code}, reason: ${event.reason})`);
    };

    socket.onerror = function (error) {
        console.error('WebSocket error:', error);
    };
}

connectWebSocket();