package com.cryptotrading.db.service;

import com.cryptotrading.db.model.User;
import com.cryptotrading.db.repository.OwnedAssetRepository;
import com.cryptotrading.db.repository.TransactionRepository;
import com.cryptotrading.db.repository.UserRepository;
import com.cryptotrading.exception.AuthenticationException;
import com.cryptotrading.utils.JwtUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

import static com.cryptotrading.utils.Constants.STARTING_BALANCE;

@Service
@Validated
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final OwnedAssetRepository ownedAssetRepository;

    public UserService(UserRepository userRepository, TransactionRepository transactionRepository,
                       OwnedAssetRepository ownedAssetRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.ownedAssetRepository = ownedAssetRepository;
    }

    public User register(@NotBlank String username, @NotBlank String passwordHash) throws AuthenticationException {
        logger.info("Registering new user: {}", username);

        if (userRepository.findByUsername(username).isPresent()) {
            logger.warn("Registration failed: Username '{}' already exists", username);
            throw new AuthenticationException("Username already taken.");
        }
        User newUser = new User(username, passwordHash, STARTING_BALANCE);
        return userRepository.save(newUser);
    }

    public String login(@NotBlank String username, @NotBlank String passwordHash,
                        @NotNull JwtUtils jwtUtils) throws AuthenticationException {
        logger.info("Attempting login for user: {}", username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AuthenticationException("User not found"));

        if (!passwordHash.equals(user.getPasswordHash())) {
            logger.warn("Login failed: Invalid password for user: {}", username);
            throw new AuthenticationException("Invalid password.");
        }

        logger.info("Login successful for user: {}", username);
        return jwtUtils.generateToken(user.getUsername());
    }

    @Transactional
    public User resetAccount(@NotBlank String username) throws AuthenticationException {
        logger.info("Resetting account for user: {}", username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AuthenticationException("User not found"));

        transactionRepository.deleteByUserId(user.getId());
        logger.info("Deleted all transactions for user: {}", username);

        ownedAssetRepository.deleteByUserId(user.getId());
        logger.info("Deleted all owned assets for user: {}", username);

        user.setBalance(STARTING_BALANCE);
        userRepository.save(user);
        logger.info("Account reset successfully for user: {}", username);

        return user;
    }

    public Optional<User> getUserByUsername(@NotBlank String username) {
        logger.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(@NotNull UUID userId) {
        logger.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId);
    }

    public void updateUserBalance(@NotNull UUID userId, @NotNull double newBalance) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setBalance(newBalance);
            userRepository.save(user);
            logger.info("Updated balance for user: {}. New balance: {}", userId, newBalance);
        } else {
            logger.warn("User not found with ID: {}", userId);
            throw new IllegalArgumentException("User not found.");
        }
    }

    public User save(@NotNull @Valid User user) {
        logger.info("Saving user: {}", user.getUsername());
        return userRepository.save(user);
    }
}
