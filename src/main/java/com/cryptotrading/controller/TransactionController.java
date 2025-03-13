package com.cryptotrading.controller;

import com.cryptotrading.exception.TransactionException;
import com.cryptotrading.model.Transaction;
import com.cryptotrading.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final UserDataService userDataService;

    @Autowired
    public TransactionController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @PostMapping
    public ResponseEntity<String> addTransaction(@RequestBody Transaction transaction) {
        try {
            userDataService.addTransaction(transaction);
            return ResponseEntity.ok("Transaction successful.");
        } catch (TransactionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @GetMapping("/user/{userId}")
//    public List<Transaction> getTransactionsByUser(@PathVariable String userId) {
//        return userDataService.getUserTransactions(userId);
//    }

//    @GetMapping("/user/{userId}/type/{type}")
//    public List<Transaction> getTransactionsByType(@PathVariable String userId, @PathVariable String type) {
//        return userDataService.getTransactionsByType(userId, type);
//    }
}