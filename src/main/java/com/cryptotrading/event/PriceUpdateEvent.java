package com.cryptotrading.event;

import com.cryptotrading.model.CryptoPrice;

import javax.validation.constraints.NotNull;

public record PriceUpdateEvent(@NotNull CryptoPrice updatedPrice) {
}