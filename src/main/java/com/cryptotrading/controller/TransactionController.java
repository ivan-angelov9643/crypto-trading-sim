package com.cryptotrading.controller;

import com.cryptotrading.db.service.TransactionService;
import com.cryptotrading.db.model.Transaction;
import com.cryptotrading.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    private final JwtUtils jwtUtils;
    @Autowired
    public TransactionController(TransactionService transactionService, JwtUtils jwtUtils) {
        this.transactionService = transactionService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<String> addTransaction(@RequestHeader(HttpHeaders.AUTHORIZATION) String header,
                                                 @RequestBody @Valid Transaction transaction) {
        if (!jwtUtils.isValidHeaderWithToken(header)) {
            logger.warn("Invalid token for add transaction request.");
            return ResponseEntity.status(401).body("Invalid token.");
        }
        String username = jwtUtils.extractUsernameFromHeader(header);
        logger.info("Received request to add transaction for user: {}", username);

        try {
            transactionService.addTransaction(username, transaction);
            logger.info("Transaction successfully added: {}", transaction);
            return ResponseEntity.ok("Transaction successful.");
        } catch (Exception e) {
            logger.error("Transaction failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Transaction error: " + e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getTransactions(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!jwtUtils.isValidHeaderWithToken(token)) {
            logger.warn("Invalid token transactions request.");
            return ResponseEntity.status(401).body("Invalid token.");
        }
        String username = jwtUtils.extractUsernameFromHeader(token);
        logger.info("Retrieving transactions for user: {}", username);
        return ResponseEntity.ok(transactionService.getTransactionsByUsername(username));
    }
}