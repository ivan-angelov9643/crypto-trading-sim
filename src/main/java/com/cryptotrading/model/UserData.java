package com.cryptotrading.model;

import com.cryptotrading.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
public class UserData {
    private static final Logger logger = LoggerFactory.getLogger(UserData.class);
    private final List<Transaction> transactions;
    private final Map<String, Asset> ownedAssets;
    @NotNull(message = "Balance cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance must be positive")
    private double balance;

    public UserData() {
        this.transactions = new ArrayList<>();
        this.ownedAssets = new HashMap<>();
        this.balance = 1000000;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Map<String, Asset> getOwnedAssets() {
        return ownedAssets;
    }

    public double getBalance() {
        return balance;
    }

    public void addTransaction(@Valid Transaction transaction) throws TransactionException {
        String type = transaction.getType().toLowerCase();
        String asset = transaction.getAsset().toUpperCase();
        double quantity = transaction.getQuantity();
        double price = transaction.getPrice();
        double cost = quantity * price;

        try {
            if ("buy".equals(type)) {
                if (balance < cost) {
                    throw new TransactionException("Insufficient balance to buy " + quantity + " " + asset +
                        ". Required: $" + cost + ", Available: $" + balance);
                }
                balance -= cost;

                if (!ownedAssets.containsKey(asset)) {
                    ownedAssets.put(asset, new Asset());
                }
                ownedAssets.get(asset).addQuantity(quantity, price);
                logger.info("Transaction successful: Bought {} {} for ${}", quantity, asset, cost);
            } else if ("sell".equals(type)) {
                if (!ownedAssets.containsKey(asset) || ownedAssets.get(asset).getQuantity() < quantity) {
                    throw new TransactionException("Insufficient " + asset + " to sell. Available: " +
                        (ownedAssets.containsKey(asset) ? ownedAssets.get(asset).getQuantity() : 0) +
                        ", Requested: " + quantity);
                }
                balance += cost;

                ownedAssets.get(asset).removeQuantity(quantity);
                logger.info("Transaction successful: Sold {} {} for ${}", quantity, asset, cost);
            } else {
                throw new TransactionException("Invalid transaction type: " + type +
                    ". Only 'buy' and 'sell' are supported.");
            }

            transactions.add(transaction);
        } catch (TransactionException e) {
            logger.error("Transaction failed: {}", e.getMessage());
        }
    }
}
