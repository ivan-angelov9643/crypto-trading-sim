function updateVisibility(symbol) {
    if (symbol == null) {
        console.error('Symbol is null or undefined');
        return;
    }

    const ownedElement = document.getElementById(`${symbol}-owned`);
    const ownedAmount = parseFloat(ownedElement.textContent.replace('$', ''));

    if (isNaN(ownedAmount)) {
        console.error(`Invalid owned amount for symbol: ${symbol}`);
        return;
    }

    if (ownedAmount <= 0) {
        ownedElement.style.visibility = 'hidden';
        console.debug(`Hiding visibility for symbol ${symbol} as owned amount is ${ownedAmount}`);
    } else {
        ownedElement.style.visibility = 'visible';
        console.debug(`Showing visibility for symbol ${symbol} as owned amount is ${ownedAmount}`);
    }
}

function observeElement(symbol) {
    if (symbol == null) {
        console.error('Symbol is null or undefined');
        return;
    }

    const ownedElement = document.getElementById(`${symbol}-owned`);
    const observer = new MutationObserver(() => {
        updateVisibility(symbol);
    });

    observer.observe(ownedElement, { childList: true, subtree: true, characterData: true });
    console.info(`Started observing element ${symbol}-owned`);
    return observer;
}

function manageOwnedVisibility() {
    const cryptoSymbols = [
        "BTC", "ETH", "USDT", "USDC", "LINK", "LTC", "XRP", "ADA", "SOL",
        "MATIC", "DOT", "DAI", "SHIB", "TRX", "AVAX", "XLM", "BCH", "FIL",
        "AAVE", "SUSHI"
    ];

    for (let i = 0; i < cryptoSymbols.length; i++) {
        const symbol = cryptoSymbols[i];

        updateVisibility(symbol);
        observeElement(symbol);
    }
}

function updateProfit(symbol) {
    if (symbol == null) {
        console.error('Symbol is null or undefined');
        return;
    }

    const ownedContainer = document.getElementById(`${symbol}-owned`);
    const quantityElement = document.getElementById(`${symbol}-owned-quantity`);
    const avgPriceElement = document.getElementById(`${symbol}-average-price`);
    const profitValueElement = document.getElementById(`${symbol}-owned-profit-value`);
    const profitPercentElement = document.getElementById(`${symbol}-owned-profit-percent`);

    if (ownedContainer.style.visibility === "hidden") {
        console.debug(`Owned container for ${symbol} is hidden.`);
        return;
    }

    const quantity = parseFloat(quantityElement?.textContent || "0");
    const avgPrice = parseFloat(avgPriceElement?.textContent.replace("$", "") || "0");

    if (isNaN(quantity) || isNaN(avgPrice)) {
        console.error(`Invalid quantity or average price for symbol: ${symbol}`);
        return;
    }

    const priceElement = document.getElementById(`${symbol}-price`);
    const currentPrice = parseFloat(priceElement.textContent.replace('$', ''));
    if (isNaN(currentPrice)) {
        console.error(`Invalid current price for symbol: ${symbol}`);
        return;
    }

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
            console.debug(`Profit for ${symbol}: $${profitValue.toFixed(2)}`);
        }

        if (profitPercentElement) {
            if (profitValue < 0) {
                profitPercentElement.textContent = `(${profitPercent.toFixed(4)}%)`;
            } else {
                profitPercentElement.textContent = `(+${profitPercent.toFixed(4)}%)`;
            }
            profitPercentElement.style.color = profitPercent >= 0 ? "green" : "red";
            console.debug(`Profit percentage for ${symbol}: ${profitPercent.toFixed(4)}%`);
        }
    } else {
        console.debug(`User does not have shares of ${symbol}`);
    }
}


document.addEventListener('DOMContentLoaded', manageOwnedVisibility)