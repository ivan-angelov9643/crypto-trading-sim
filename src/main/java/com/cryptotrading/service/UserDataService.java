package com.cryptotrading.service;

import com.cryptotrading.exception.TransactionException;
import com.cryptotrading.model.Asset;
import com.cryptotrading.model.Transaction;
import com.cryptotrading.model.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Validated
@Service
public class UserDataService {
    private static final Logger logger = LoggerFactory.getLogger(UserDataService.class);
    private final Map<String, UserData> usersData = new ConcurrentHashMap<>();

    public void addTransaction(@Valid @NotNull Transaction transaction) throws TransactionException {
        logger.info("Adding transaction for user: {}", transaction.getUserId());
        UserData account = usersData.computeIfAbsent(transaction.getUserId(), k -> new UserData());
        account.addTransaction(transaction);
    }

    public UserData getUserData(@NotNull @NotEmpty String userId) {
        logger.info("Retrieving data for user: {}", userId);
        return usersData.getOrDefault(userId, new UserData());
    }

    public Asset getUserAsset(@NotNull @NotEmpty String userId, @NotNull @NotEmpty String asset) {
        logger.info("Retrieving asset '{}' for user: {}", asset, userId);
        return usersData.getOrDefault(userId, new UserData()).getOwnedAssets().get(asset);
    }

    public double getUserBalance(@NotNull @NotEmpty String userId) {
        logger.info("Retrieving balance for user: {}", userId);
        return usersData.getOrDefault(userId, new UserData()).getBalance();
    }
}
