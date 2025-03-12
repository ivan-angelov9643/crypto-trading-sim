package com.cryptotrading.controller;

import com.cryptotrading.exception.TransactionException;
import com.cryptotrading.model.Transaction;
import com.cryptotrading.model.UserData;
import com.cryptotrading.service.UserDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users-data")
// TODO make separate controllers for userdata and transactions
public class UserDataController {
    private final UserDataService userDataService;

    public UserDataController(UserDataService userDataService) {
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

    @GetMapping("/{userId}")
    public UserData getUserData(@PathVariable String userId) {
        return userDataService.getUserData(userId);
    }
}
