function getQuantityInput() {
    return document.getElementById("modal-quantity");
}

function getValueInput() {
    return document.getElementById("modal-value");
}

function getModalPriceElement() {
    return document.getElementById("modal-price");
}

function getModalPrice() {
    const priceText = getModalPriceElement().textContent.replace("$", "");
    return parseFloat(priceText) || 0;
}

function updateValueFromQuantity() {
    const price = getModalPrice();
    const quantity = parseFloat(getQuantityInput().value) || 0;
    getValueInput().value = (quantity * price).toFixed(2);
}

function updateQuantityFromValue() {
    const price = getModalPrice();
    const value = parseFloat(getValueInput().value) || 0;
    getQuantityInput().value = (value / price).toFixed(6);
}

function setupInputListeners () {
    const quantityInput = getQuantityInput();
    const valueInput = getValueInput();

    quantityInput.addEventListener("input", function () {
        updateValueFromQuantity();
    });

    valueInput.addEventListener("input", function () {
        updateQuantityFromValue();
    });
}

document.addEventListener("DOMContentLoaded", setupInputListeners);