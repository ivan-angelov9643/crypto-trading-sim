package com.cryptotrading.model;

public class CryptoPrice {
    private String name;
    private String symbol;
    private String price;

    public CryptoPrice(String name, String symbol, String price) {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "CryptoPrice{" +
            "name='" + name + '\'' +
            ", symbol='" + symbol + '\'' +
            ", price='" + price + '\'' +
            '}';
    }
}