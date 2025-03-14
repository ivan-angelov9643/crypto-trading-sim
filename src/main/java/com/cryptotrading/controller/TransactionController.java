package com.cryptotrading.controller;

import com.cryptotrading.exception.TransactionException;
import com.cryptotrading.model.Transaction;
import com.cryptotrading.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final UserDataService userDataService;

    @Autowired
    public TransactionController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @PostMapping
    public ResponseEntity<String> addTransaction(@RequestBody @Valid Transaction transaction) {
        logger.info("Received request to add transaction: {}", transaction);

        try {
            userDataService.addTransaction(transaction);
            logger.info("Transaction successfully added: {}", transaction);
            return ResponseEntity.ok("Transaction successful.");
        } catch (TransactionException e) {
            logger.error("Transaction failed due to application error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Transaction error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while adding transaction: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error. Please try again later.");
        }
    }
}