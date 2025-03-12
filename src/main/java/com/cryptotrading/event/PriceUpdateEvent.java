package com.cryptotrading.event;

import com.cryptotrading.model.CryptoPrice;
import java.util.Map;

public record PriceUpdateEvent(Map<String, CryptoPrice> prices) {
}