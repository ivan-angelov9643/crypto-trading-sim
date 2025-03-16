package com.cryptotrading.db.model;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.UUID;

import static com.cryptotrading.utils.Constants.EPSILON;

@Entity
@Table(name = "owned_assets")
@Validated
public class OwnedAsset {
    private static final Logger logger = LoggerFactory.getLogger(OwnedAsset.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Valid
    private User user;

    @Column(nullable = false)
    @NotNull(message = "Asset symbol cannot be null")
    @Size(min = 1, max = 10, message = "Asset symbol must be between 1 and 10 characters")
    private String assetSymbol;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = true, message = "Quantity must be greater than or equal to zero")
    private double quantity;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = true, message = "Average price must be greater than or equal to zero")
    private double averagePrice;

    public OwnedAsset() {}

    public OwnedAsset(User user, String assetSymbol) {
        this.user = user;
        this.assetSymbol = assetSymbol;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void addQuantity(@Positive(message = "New quantity must be greater than zero.") double newQuantity,
                            @DecimalMin(value = "0.0", message = "New price must be at least zero.") double newPrice) {
        if (newQuantity <= 0 || newPrice < 0) {
            logger.error("Invalid quantity or price: newQuantity = {}, newPrice = {}", newQuantity, newPrice);
            throw new IllegalArgumentException("Invalid quantity or price.");
        }

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
        if (Math.abs(this.quantity) < EPSILON) {
            this.averagePrice = 0.0;
        }

        logger.info("Removed quantity: {}, new quantity: {}, averagePrice: {}", quantityToRemove, this.quantity, this.averagePrice);
    }
}
