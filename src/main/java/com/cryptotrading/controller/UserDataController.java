package com.cryptotrading.controller;

    import com.cryptotrading.model.Asset;
    import com.cryptotrading.model.UserData;
    import com.cryptotrading.service.UserDataService;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;

    import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/users-data")
public class UserDataController {
    private static final Logger logger = LoggerFactory.getLogger(UserDataController.class);
    private final UserDataService userDataService;

    @Autowired
    public UserDataController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @GetMapping("/{userId}")
    public UserData getUserData(@PathVariable @NotNull String userId) {
        logger.info("Request to get data for user: {}", userId);
        return userDataService.getUserData(userId);
    }
    @GetMapping("/{userId}/assets/{asset}")
    public Asset getUserAsset(@PathVariable @NotNull String userId, @PathVariable @NotNull String asset) {
        logger.info("Request to get asset '{}' for user: {}", asset, userId);
        return userDataService.getUserAsset(userId, asset);
    }

    @GetMapping("/{userId}/balance")
    public double getUserBalance(@PathVariable @NotNull String userId) {
        logger.info("Request to get balance for user: {}", userId);
        return userDataService.getUserBalance(userId);
    }
}