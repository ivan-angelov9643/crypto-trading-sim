package com.cryptotrading.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

// TODO add datetime of transaction
public class Transaction {

    @NotNull(message = "User ID cannot be null.")
    private String userId;

    @NotNull(message = "Transaction type cannot be null.")
    @Pattern(regexp = "^(buy|sell)$", message = "Transaction type must be 'buy' or 'sell'.")
    private String type;

    @NotNull(message = "Asset cannot be null.")
    private String asset;

    @Positive(message = "Quantity must be greater than zero.")
    private double quantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be at least 0.")
    private double price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Value must be at least 0.")
    private double value;

    public Transaction(String userId, String type, String asset, double quantity, double price, double value) {
        this.userId = userId;
        this.type = type;
        this.asset = asset;
        this.quantity = quantity;
        this.price = price;
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "userId='" + userId + '\'' +
            ", type='" + type + '\'' +
            ", asset='" + asset + '\'' +
            ", quantity=" + quantity +
            ", price=" + price +
            '}';
    }
}