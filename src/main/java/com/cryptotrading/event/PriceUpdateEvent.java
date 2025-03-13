package com.cryptotrading.event;

import com.cryptotrading.model.CryptoPrice;

public record PriceUpdateEvent(CryptoPrice updatedPrice) {
}