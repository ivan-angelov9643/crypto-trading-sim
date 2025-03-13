function updatePrice(crypto) {
    const priceElement = document.getElementById(`${crypto.symbol}-price`);
    const ownedContainer = document.getElementById(`${crypto.symbol}-owned`);
    const quantityElement = document.getElementById(`${crypto.symbol}-owned-quantity`);
    const avgPriceElement = document.getElementById(`${crypto.symbol}-average-price`);
    const profitValueElement = document.getElementById(`${crypto.symbol}-owned-profit-value`);
    const profitPercentElement = document.getElementById(`${crypto.symbol}-owned-profit-percent`);

    if (priceElement) {
        priceElement.textContent = `$${crypto.price}`;
    }

    if (ownedContainer && ownedContainer.style.display !== "none") {
        const quantity = parseFloat(quantityElement?.textContent || "0");
        const avgPrice = parseFloat(avgPriceElement?.textContent.replace("$", "") || "0");
        const currentPrice = parseFloat(crypto.price);

        if (quantity > 0 && avgPrice > 0) {
            const totalCost = quantity * avgPrice;
            const currentValue = quantity * currentPrice;
            const profitValue = currentValue - totalCost;
            const profitPercent = ((currentPrice - avgPrice) / avgPrice) * 100;

            if (profitValueElement) {
                if (profitValue < 0) {
                    profitValueElement.textContent = `-$${-profitValue.toFixed(2)}`;
                } else {
                    profitValueElement.textContent = `+$${profitValue.toFixed(2)}`;
                }
                profitValueElement.style.color = profitValue >= 0 ? "green" : "red";
            }

            if (profitPercentElement) {
                if (profitValue < 0) {
                    profitPercentElement.textContent = `(${profitPercent.toFixed(4)}%)`;
                } else {
                    profitPercentElement.textContent = `(+${profitPercent.toFixed(4)}%)`;
                }
                profitPercentElement.style.color = profitPercent >= 0 ? "green" : "red";
            }
        }
    }
}

function updatePrices(prices) {
    for (const [symbol, crypto] of Object.entries(prices)) {
        updatePrice(crypto);
    }
}

const socket = new WebSocket('ws://localhost:8080/crypto-prices');

socket.onmessage = function(event) {
//    console.log('Received: ' + event.data);

    try {
        const data = JSON.parse(event.data);

        if (data.hasOwnProperty("name") && data.hasOwnProperty("symbol") && data.hasOwnProperty("price")) {
            updatePrice(data);
        } else {
            updatePrices(data);
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