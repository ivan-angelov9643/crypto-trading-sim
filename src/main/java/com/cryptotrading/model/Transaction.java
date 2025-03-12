package com.cryptotrading.model;

// TODO add datetime of transaction
public class Transaction {
    private String userId;
    private String type; // "buy" or "sell"
    private String asset;
    private double quantity;
    private double price;
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