document.addEventListener("DOMContentLoaded", function () {
    const quantityInput = document.getElementById("modal-quantity");
    const valueInput = document.getElementById("modal-value");

    quantityInput.addEventListener("input", function () {
        updateValueFromQuantity();
    });

    valueInput.addEventListener("input", function () {
        updateQuantityFromValue();
    });
});

function updateValueFromQuantity() {
    const price = getModalPrice();
    const quantity = parseFloat(document.getElementById("modal-quantity").value) || 0;
    document.getElementById("modal-value").value = (quantity * price).toFixed(2);
}

function updateQuantityFromValue() {
    const price = getModalPrice();
    const value = parseFloat(document.getElementById("modal-value").value) || 0;
    document.getElementById("modal-quantity").value = (value / price).toFixed(6);
}

function getModalPrice() {
    const priceText = document.getElementById("modal-price").textContent.replace("$", "");
    return parseFloat(priceText) || 0;
}
