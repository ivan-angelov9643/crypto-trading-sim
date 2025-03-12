package com.cryptotrading.model;

import com.cryptotrading.utils.Constants;

public class Asset {
    private double quantity;
    private double averagePrice;

    public Asset() {
        this.quantity = 0.0;
        this.averagePrice = 0.0;
    }

    public Asset(double quantity, double averagePrice) {
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void addQuantity(double newQuantity, double newPrice) {
        if (newQuantity <= 0 || newPrice <= 0) {
            throw new IllegalArgumentException("Quantity and price must be positive.");
        }

        double totalValue = (this.quantity * this.averagePrice) + (newQuantity * newPrice);
        this.quantity += newQuantity;
        this.averagePrice = totalValue / this.quantity;
    }

    public void removeQuantity(double quantityToRemove) {
        if (quantityToRemove <= 0 || quantityToRemove > this.quantity) {
            throw new IllegalArgumentException("Invalid quantity to remove.");
        }

        this.quantity -= quantityToRemove;
        if (Math.abs(this.quantity) < Constants.EPSILON) {
            this.averagePrice = 0.0;
        }
    }

    @Override
    public String toString() {
        return "Asset{" +
            "quantity=" + quantity +
            ", averagePrice=$" + String.format("%.2f", averagePrice) +
            '}';
    }
}