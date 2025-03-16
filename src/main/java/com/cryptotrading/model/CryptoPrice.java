package com.cryptotrading.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CryptoPrice {
    @NotNull(message = "Name cannot be null.")
    @NotBlank(message = "Name cannot be empty.")
    private String name;
    @NotNull(message = "Symbol cannot be null.")
    @NotBlank(message = "Symbol cannot be empty.")
    @Pattern(regexp = "^[A-Za-z0-9]{3,5}$", message = "Symbol should be alphanumeric with 3-5 characters.")
    private String symbol;
    @NotNull(message = "Price cannot be null.")
    @Pattern(regexp = "^\\d+(\\.\\d*)?$", message = "Price must be a valid number with up to 2 decimal places.")
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