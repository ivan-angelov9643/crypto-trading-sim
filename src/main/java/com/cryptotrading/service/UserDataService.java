package com.cryptotrading.service;

import com.cryptotrading.exception.TransactionException;
import com.cryptotrading.model.Transaction;
import com.cryptotrading.model.UserData;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class UserDataService {
    private final Map<String, UserData> usersData = new ConcurrentHashMap<>();

    public void addTransaction(Transaction transaction) throws TransactionException {
        UserData account = usersData.computeIfAbsent(transaction.getUserId(), k -> new UserData());
        account.addTransaction(transaction);
    }

    public UserData getUserData(String userId) {
        return usersData.getOrDefault(userId, new UserData());
    }
}
