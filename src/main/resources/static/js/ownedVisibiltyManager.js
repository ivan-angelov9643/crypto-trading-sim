function updateVisibility(element) {
    if (parseFloat(element.textContent.replace('$', '')) <= 0) {
        element.style.visibility = 'hidden';
    } else {
        element.style.visibility = 'visible';
    }
}

function observeElement(element) {
    const observer = new MutationObserver(() => {
        updateVisibility(element);
    });

    observer.observe(element, { childList: true, subtree: true, characterData: true });
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
        const ownedElement = document.getElementById(`${symbol}-owned`);

        updateVisibility(ownedElement);
        observeElement(ownedElement);
    }
}

document.addEventListener('DOMContentLoaded', manageOwnedVisibility)