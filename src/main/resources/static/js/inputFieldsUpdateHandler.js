function getQuantityInput() {
    return document.getElementById("modal-quantity");
}

function getValueInput() {
    return document.getElementById("modal-value");
}

function getModalPriceElement() {
    return document.getElementById("modal-price");
}
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
    const price = parseFloat(priceText);

    if (isNaN(price)) {
        console.error("Invalid price format: ", priceText);
        return 0;
    }

    return price;
}

function updateValueFromQuantity() {
    const price = getModalPrice();
    const quantity = parseFloat(getQuantityInput().value) || 0;

    if (quantity < 0) {
        console.warn("Invalid quantity input: ", getQuantityInput().value);
    }

    const value = (quantity * price).toFixed(2);
    getValueInput().value = value;
    console.info(`Updated value to: $${value} for quantity: ${quantity}`);
}

function updateQuantityFromValue() {
    const price = getModalPrice();
    const value = parseFloat(getValueInput().value) || 0;

    if (value < 0) {
        console.warn("Invalid value input: ", getValueInput().value);
    }

    const quantity = (value / price).toFixed(6);
    getQuantityInput().value = quantity;
    console.info(`Updated quantity to: ${quantity} for value: $${value}`);
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