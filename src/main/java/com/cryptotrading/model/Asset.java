package com.cryptotrading.model;

import com.cryptotrading.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Positive;

@Validated
public class Asset {
    private static final Logger logger = LoggerFactory.getLogger(Asset.class);
    @Positive(message = "Quantity must be greater than zero.")
    private double quantity;
    @DecimalMin(value = "0.0", inclusive = true, message = "Average price must be at least zero.")
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

    public void addQuantity(@Positive(message = "New quantity must be greater than zero.") double newQuantity,
                            @DecimalMin(value = "0.0", message = "New price must be at least zero.") double newPrice) {
        double totalValue = (this.quantity * this.averagePrice) + (newQuantity * newPrice);
        this.quantity += newQuantity;
        this.averagePrice = totalValue / this.quantity;

        logger.info("Added quantity: {} at price: {}, new quantity: {}, new averagePrice: {}",
            newQuantity, newPrice, this.quantity, this.averagePrice);
    }

    public void removeQuantity(@Positive(message = "Quantity to remove must be greater than zero.") double quantityToRemove) {
        if (quantityToRemove > this.quantity) {
            logger.error("Invalid remove request: quantityToRemove = {} exceeds available quantity = {}",
                quantityToRemove, this.quantity);
            throw new IllegalArgumentException("Invalid quantity to remove.");
        }

        this.quantity -= quantityToRemove;
        if (Math.abs(this.quantity) < Constants.EPSILON) {
            this.averagePrice = 0.0;
        }

        logger.info("Removed quantity: {}, new quantity: {}, averagePrice: {}", quantityToRemove, this.quantity, this.averagePrice);
    }

    @Override
    public String toString() {
        return "Asset{" +
            "quantity=" + quantity +
            ", averagePrice=$" + String.format("%.2f", averagePrice) +
            '}';
    }
}