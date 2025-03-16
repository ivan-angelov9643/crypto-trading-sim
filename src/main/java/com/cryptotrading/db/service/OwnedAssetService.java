package com.cryptotrading.db.service;

import com.cryptotrading.db.model.OwnedAsset;
import com.cryptotrading.db.model.User;
import com.cryptotrading.db.repository.OwnedAssetRepository;
import com.cryptotrading.db.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Service
public class OwnedAssetService {
    private static final Logger logger = LoggerFactory.getLogger(OwnedAssetService.class);
    private final OwnedAssetRepository ownedAssetRepository;
    private final UserRepository userRepository;

    @Autowired
    public OwnedAssetService(OwnedAssetRepository ownedAssetRepository, UserRepository userRepository) {
        this.ownedAssetRepository = ownedAssetRepository;
        this.userRepository = userRepository;
    }

    public Optional<OwnedAsset> getAssetByUsernameAndSymbol(@NotBlank String username, @NotBlank String symbol) {
        logger.info("Retrieving asset '{}' for user: {}", symbol, username);
        return ownedAssetRepository.findByUserUsernameAndAssetSymbol(username, symbol);
    }

    public List<OwnedAsset> getAssetsByUsername(@NotBlank String username) {
        logger.info("Retrieving assets for user: {}", username);
        return ownedAssetRepository.findByUserUsername(username);
    }

    public void addAssetByUsernameAndSymbol(@NotBlank String username, @NotBlank String symbol,
                                            @DecimalMin(value = "0.0", inclusive = true,
                                                message = "Quantity must be greater than or equal to zero")
                                            double quantity,
                                            @DecimalMin(value = "0.0", inclusive = true,
                                                message = "Price must be greater than or equal to zero")
                                            double price) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + username));

        OwnedAsset asset = ownedAssetRepository.findByUserUsernameAndAssetSymbol(username, symbol)
            .orElse(new OwnedAsset(user, symbol));

        asset.addQuantity(quantity, price);
        ownedAssetRepository.save(asset);

        logger.info("Added asset: {} for user: {}. New quantity: {}, Average price: {}",
            symbol, username, asset.getQuantity(), asset.getAveragePrice());
    }

    public void removeAssetQuantity(@NotBlank String username, @NotBlank String symbol,
                                    @DecimalMin(value = "0.0", inclusive = true,
                                        message = "Quantity must be greater than or equal to zero")
                                    double quantity) {
        OwnedAsset asset = ownedAssetRepository.findByUserUsernameAndAssetSymbol(username, symbol)
            .orElseThrow(() -> new IllegalArgumentException("Asset not found"));

        asset.removeQuantity(quantity);
        ownedAssetRepository.save(asset);

        logger.info("Removed asset quantity: {} for user: {}. Remaining quantity: {}",
            quantity, username, asset.getQuantity());
    }

    public List<OwnedAsset> getUserAssets(@NotBlank String username) {
        logger.info("Retrieving all assets for user: {}", username);
        return ownedAssetRepository.findByUserUsername(username);
    }
}
