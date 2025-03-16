package com.cryptotrading.db.repository;

import com.cryptotrading.db.model.OwnedAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface OwnedAssetRepository extends JpaRepository<OwnedAsset, UUID> {
    List<OwnedAsset> findByUserUsername(String username);

    Optional<OwnedAsset> findByUserUsernameAndAssetSymbol(String username, String symbol);

    void deleteByUserId(UUID id);
}

