package com.cryptotrading.controller;

import com.cryptotrading.db.model.OwnedAsset;
import com.cryptotrading.db.model.Transaction;
import com.cryptotrading.db.model.User;
import com.cryptotrading.db.service.OwnedAssetService;
import com.cryptotrading.db.service.TransactionService;
import com.cryptotrading.db.service.UserService;
import com.cryptotrading.exception.AuthenticationException;
import com.cryptotrading.model.UserCredentials;
import com.cryptotrading.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.cryptotrading.utils.PasswordHasher.hashPassword;

@Validated
@RestController
@RequestMapping("/")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final OwnedAssetService ownedAssetService;
    private final TransactionService transactionService;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserController(UserService userService, OwnedAssetService ownedAssetService,
                          TransactionService transactionService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.ownedAssetService = ownedAssetService;
        this.transactionService = transactionService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserCredentials credentials) {
        logger.info("Registering new user: {}", credentials.getUsername());

        try {
            String hashedPassword = hashPassword(credentials.getPassword());
            User newUser = userService.register(credentials.getUsername(), hashedPassword);

            return ResponseEntity.ok(newUser);

        } catch (AuthenticationException e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserCredentials credentials) {
        logger.info("User login attempt: {}", credentials.getUsername());

        try {
            String hashedPassword = hashPassword(credentials.getPassword());
            String token = userService.login(credentials.getUsername(), hashedPassword, jwtUtils);
            logger.info("Successfully logged user: {}", credentials.getUsername());
            return ResponseEntity.ok(token);

        } catch (AuthenticationException e) {
            logger.warn("Login failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        logger.info("User logout attempt");

        if (!jwtUtils.isValidHeaderWithToken(header)) {
            logger.warn("Invalid authorization header for logout request.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization header.");
        }

        String username = jwtUtils.extractUsernameFromHeader(header);
        logger.info("Logging out user: {}", username);

        jwtUtils.invalidateToken(header);

        return ResponseEntity.ok("User logged out successfully");
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        logger.info("User reset account attempt");

        if (!jwtUtils.isValidHeaderWithToken(header)) {
            logger.warn("Invalid authorization header for reset request.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization header.");
        }

        String username = jwtUtils.extractUsernameFromHeader(header);
        logger.info("Resetting account for user: {}", username);

        try {
            User resetUser = userService.resetAccount(username);
            logger.info("Account reset successfully for user: {}", username);

            return ResponseEntity.ok("Account reset successfully for user: " + resetUser.getUsername());
        } catch (AuthenticationException e) {
            logger.error("Account reset failed for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserData(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        if (!jwtUtils.isValidHeaderWithToken(header)) {
            logger.warn("Invalid authorization header for user data request.");
            return ResponseEntity.status(401).body("Invalid authorization header.");
        }

        String username = jwtUtils.extractUsernameFromHeader(header);
        logger.info("Request to get data for user: {}", username);

        Optional<User> userOptional = userService.getUserByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.info("User found: {}", username);

            List<OwnedAsset> ownedAssets = ownedAssetService.getAssetsByUsername(username);
            List<Transaction> transactions = transactionService.getTransactionsByUsername(username);

            Map<String, Object> response = new HashMap<>();
            response.put("balance", user.getBalance());
            response.put("ownedAssets", ownedAssets);
            response.put("transactions", transactions);

            return ResponseEntity.ok(response);
        } else {
            logger.warn("User not found: {}", username);
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @GetMapping("/assets/{asset}")
    public ResponseEntity<?> getUserAsset(@RequestHeader(HttpHeaders.AUTHORIZATION) String header, @PathVariable @NotNull String asset) {
        if (!jwtUtils.isValidHeaderWithToken(header)) {
            logger.warn("Invalid authorization header for asset request.");
            return ResponseEntity.status(401).body("Invalid authorization header.");
        }
        String username = jwtUtils.extractUsernameFromHeader(header);
        logger.info("Request to get asset '{}' for user: {}", asset, username);

        Optional<OwnedAsset> assetOptional = ownedAssetService.getAssetByUsernameAndSymbol(username, asset);

        if (assetOptional.isPresent()) {
            logger.info("Asset '{}' found for user: {}", asset, username);
            return ResponseEntity.ok(assetOptional.get());
        } else {
            logger.warn("Asset '{}' not found for user: {}", asset, username);
            return ResponseEntity.status(404).body("Asset not found");
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getUserBalance(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        if (!jwtUtils.isValidHeaderWithToken(header)) {
            logger.warn("Invalid authorization header for balance request.");
            return ResponseEntity.status(401).body("Invalid authorization header.");
        }
        String username = jwtUtils.extractUsernameFromHeader(header);
        logger.info("Request to get balance for user: {}", username);

        Optional<User> userOptional = userService.getUserByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.info("Balance retrieved for user: {}", username);
            return ResponseEntity.ok(user.getBalance());
        } else {
            logger.warn("User not found for balance request: {}", username);
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {
        logger.info("Validating token");

        if (!jwtUtils.isValidHeaderWithToken(header)) {
            logger.info("Token validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token, or user does not exist");
        }

        logger.info("Token validated");
        return ResponseEntity.ok("Token is valid and associated with an existing user");
    }
}