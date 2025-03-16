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
    if (title == null) {
        console.error('Title is null or undefined');
        return;
    }
    if (type == null) {
        console.error('Type is null or undefined');
        return;
    }
    if (currency == null) {
        console.error('Currency is null or undefined');
        return;
    }

    title.textContent = `${type.toUpperCase()} ${currency}`;
}

function updateModalPrice(priceElement, modalPriceElement) {
    if (priceElement == null) {
        console.error('Price element is null or undefined');
        return;
    }
    if (modalPriceElement == null) {
        console.error('Modal price element is null or undefined');
        return;
    }

    const updatedPrice = priceElement.textContent.replace('$', '');
    modalPriceElement.textContent = `$${updatedPrice}`;
    updateValueFromQuantity();
}

function createPriceObserver(priceElement, modalPriceElement) {
    if (priceElement == null) {
        console.error('Price element is null or undefined');
        return;
    }
    if (modalPriceElement == null) {
        console.error('Modal price element is null or undefined');
        return;
    }

    const priceObserver = new MutationObserver(() => updateModalPrice(priceElement, modalPriceElement));
    priceObserver.observe(priceElement, { childList: true, subtree: true });
    return priceObserver;
}

async function sendTransaction(transaction) {
    if (transaction == null) {
        console.error('Transaction is null or undefined');
        return;
    }

    try {
        const token = localStorage.getItem('jwtToken');
        const response = await fetch('/transactions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify(transaction)
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error('Transaction failed:', errorText);
            showNotification(errorText, "error");
            return null;
        }

        return await response.text();
    } catch (error) {
        console.error("Error sending transaction:", error);
        showNotification("Transaction failed. Please try again.", "error");
        return null;
    }
}

function closeModal(priceObserver) {
    if (priceObserver == null) {
        console.error('Price observer is null or undefined');
        return;
    }

    document.getElementById("modal-value").value = '';
    document.getElementById("modal-quantity").value = '';
    getTransactionModal().style.display = 'none';
    priceObserver.disconnect();
}

function showNotification(message, type = "success", duration = 3000) {
    if (message == null) {
        console.error('Message observer is null or undefined');
        return;
    }

    const container = document.getElementById("notification-container");
    const notification = document.createElement("div");

    notification.classList.add("notification", type);
    notification.textContent = message;
    container.appendChild(notification);

    setTimeout(() => {
        notification.classList.add("hide");
        setTimeout(() => container.removeChild(notification), 500);
    }, duration);
}

function setupConfirmTransactionButton(modalPriceElement, type, currency, priceObserver) {
    if (modalPriceElement == null) {
        console.error('Modal price element is null or undefined');
        return;
    }
    if (type == null) {
        console.error('Type is null or undefined');
        return;
    }
    if (currency == null) {
        console.error('Currency is null or undefined');
        return;
    }
    if (priceObserver == null) {
        console.error('Price observer is null or undefined');
        return;
    }

    document.getElementById('confirm-transaction').onclick = async function () {
        const quantity = parseFloat(document.getElementById('modal-quantity').value);
        const value = parseFloat(document.getElementById('modal-value').value);
        const currentPrice = parseFloat(modalPriceElement.textContent.replace('$', ''));

        if (!quantity || quantity <= 0) {
            console.warn("Invalid quantity entered:", quantity);
            showNotification("Please enter a valid quantity.", "error");
            return;
        }

        if (!value || value <= 0) {
            console.warn("Invalid value entered:", value);
            showNotification("Please enter a valid value.", "error");
            return;
        }

        if (isNaN(currentPrice) || currentPrice <= 0) {
            console.warn("Invalid price detected:", currentPrice);
            showNotification("Transaction failed. Please try again.", "error");
            return;
        }

        const transaction = {
           type: type,
           quantity: quantity,
           price: currentPrice,
           total: value,
           assetSymbol: currency
        };

        console.info('Transaction details:', transaction);

        const result = await sendTransaction(transaction);
        if (result) {
            showNotification("Transaction successful!");
            fetchAndUpdateUserBalance();
            await fetchAndUpdateUserAsset(currency);
            updateProfit(currency);
            addTransactionToHistory(transaction);
        }

        closeModal(priceObserver);
    };
}

function setupCancelTransactionButton(priceObserver) {
    if (priceObserver == null) {
        console.error('Price observer is null or undefined');
        return;
    }
    document.getElementById('cancel-transaction').onclick = () => closeModal(priceObserver);
    document.querySelector('.close-btn').onclick = () => closeModal(priceObserver);
}

function handleTransaction(type, currency) {
    if (type == null) {
        console.error('Type is null or undefined');
        return;
    }
    if (currency == null) {
        console.error('Currency is null or undefined');
        return;
    }

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