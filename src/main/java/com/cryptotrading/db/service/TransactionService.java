package com.cryptotrading.db.service;

import com.cryptotrading.db.model.OwnedAsset;
import com.cryptotrading.db.model.Transaction;
import com.cryptotrading.db.model.User;
import com.cryptotrading.db.repository.TransactionRepository;
import com.cryptotrading.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final OwnedAssetService ownedAssetService;
    private final UserService userService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              OwnedAssetService ownedAssetService,
                              UserService userService) {
        this.transactionRepository = transactionRepository;
        this.ownedAssetService = ownedAssetService;
        this.userService = userService;
    }

    public void addTransaction(@NotBlank String username, @Valid @NotNull Transaction transaction) throws TransactionException {
        String assetSymbol = transaction.getAssetSymbol();
        double quantity = transaction.getQuantity();
        double price = transaction.getPrice();
        double cost = quantity * price;

        User user = userService.getUserByUsername(username).orElseThrow(() -> new TransactionException("User not found"));
        transaction.setUser(user);
        if ("buy".equals(transaction.getType())) {
            if (user.getBalance() < cost) {
                throw new TransactionException("Insufficient balance for purchase.");
            }
            user.setBalance(user.getBalance() - cost);
            userService.save(user);

            ownedAssetService.addAssetByUsernameAndSymbol(username, assetSymbol, quantity, price);
        } else if ("sell".equals(transaction.getType())) {
            OwnedAsset ownedAsset = ownedAssetService.getAssetByUsernameAndSymbol(username, assetSymbol)
                .orElseThrow(() -> new TransactionException("No asset found to sell."));
            if (ownedAsset.getQuantity() < quantity) {
                throw new TransactionException("Insufficient asset quantity for sale.");
            }

            user.setBalance(user.getBalance() + cost);
            userService.save(user);

            ownedAssetService.removeAssetQuantity(username, assetSymbol, quantity);
        } else {
            throw new TransactionException("Invalid transaction type: " + transaction.getType());
        }

        transactionRepository.save(transaction);
        logger.info("Transaction processed: {}", transaction);
    }

    public List<Transaction> getTransactionsByUsername(@NotNull String username) {
        logger.info("Retrieving transactions for user: {}", username);
        User user = userService.getUserByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return transactionRepository.findByUserId(user.getId());
    }
}
