package com.cryptotrading.db.model;

import jakarta.persistence.*;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Valid
    private User user;

    @Column(nullable = false)
    @NotNull(message = "Transaction type cannot be null")
    @Pattern(regexp = "^(buy|sell)$", message = "Transaction type must be 'buy' or 'sell'.")
    private String type;

    @Column(nullable = false)
    @NotNull(message = "Asset symbol cannot be null")
    @Size(min = 1, max = 10, message = "Asset symbol must be between 1 and 10 characters")
    private String assetSymbol;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = true, message = "Quantity must be greater than or equal to zero")
    private double quantity;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to zero")
    private double price;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = true, message = "Total must be greater than or equal to zero")
    private double total;

    public Transaction() {}

    public Transaction(User user, String type, double quantity, double price, LocalDateTime datetime, String assetSymbol) {
        this.user = user;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.total = quantity * price;
        this.assetSymbol = assetSymbol;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotal() {
        return total;
    }
    public String getAssetSymbol() {
        return assetSymbol;
    }
}
