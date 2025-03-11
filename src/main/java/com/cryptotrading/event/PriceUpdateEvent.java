package com.cryptotrading.event;

import com.cryptotrading.model.CryptoPrice;
import java.util.Map;

public class PriceUpdateEvent {
    private final Map<String, CryptoPrice> prices;

    public PriceUpdateEvent(Map<String, CryptoPrice> prices) {
        this.prices = prices;
    }

    public Map<String, CryptoPrice> getPrices() {
        return prices;
    }
}