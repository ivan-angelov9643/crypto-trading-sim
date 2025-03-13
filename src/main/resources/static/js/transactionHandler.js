function getTransactionModal() {
    return document.getElementById('transaction-modal');
}

function getModalTitle() {
    return document.getElementById('modal-title');
}

function getPriceElement(currency) {
    return document.getElementById(`${currency}-price`);
}

function getModalPriceElement() {
    return document.getElementById('modal-price');
}

function setModalTitle(title, type, currency) {
    title.textContent = `${type.toUpperCase()} ${currency}`;
}

function updateModalPrice(priceElement, modalPriceElement) {
    const updatedPrice = priceElement.textContent.replace('$', '');
    modalPriceElement.textContent = `$${updatedPrice}`;
    updateValueFromQuantity();
}

function createPriceObserver(priceElement, modalPriceElement) {
    const priceObserver = new MutationObserver(() => updateModalPrice(priceElement, modalPriceElement));
    priceObserver.observe(priceElement, { childList: true, subtree: true });
    return priceObserver;
}

async function sendTransaction(transaction) {
    return await fetch('/transactions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(transaction)
    });
}

function closeModal(priceObserver) {
    document.getElementById("modal-value").value = '';
    document.getElementById("modal-quantity").value = '';
    getTransactionModal().style.display = 'none';
    priceObserver.disconnect();
}

function setupConfirmTransactionButton(modalPriceElement, type, currency, priceObserver) {
    document.getElementById('confirm-transaction').onclick = async function () {
        const quantity = parseFloat(document.getElementById('modal-quantity').value);
        const value = parseFloat(document.getElementById('modal-value').value);
        const currentPrice = parseFloat(modalPriceElement.textContent.replace('$', ''));

        if (isNaN(quantity) || quantity <= 0) {
            alert("Please enter a valid quantity.");
            return;
        }

        const transaction = {
           userId: window.userId,
           type: type,
           quantity: quantity,
           price: currentPrice,
           value: value,
           asset: currency
       };

        try {
            const response = await sendTransaction(transaction);
            const result = await response.text();

            if (!response.ok) {
                alert(result);
            } else {
                alert("Transaction successful!");
                fetchAndUpdateUserBalance();
                fetchAndUpdateUserAsset(currency);
                addTransactionToHistory(transaction);
            }
        } catch (error) {
            console.error("Error processing transaction:", error);
        }
        closeModal(priceObserver);
    };
}

function setupCancelTransactionButton(priceObserver) {
    document.getElementById('cancel-transaction').onclick = () => closeModal(priceObserver);
    document.querySelector('.close-btn').onclick = () => closeModal(priceObserver);
}

function handleTransaction(type, currency) {
    const modal = getTransactionModal();
    const title = getModalTitle();
    const priceElement = getPriceElement(currency);
    const modalPriceElement = getModalPriceElement();

    setModalTitle(title, type, currency);
    updateModalPrice(priceElement, modalPriceElement);
    modal.style.display = 'block';

    const priceObserver = createPriceObserver(priceElement, modalPriceElement);
    setupConfirmTransactionButton(modalPriceElement, type, currency, priceObserver);
    setupCancelTransactionButton(priceObserver);
}