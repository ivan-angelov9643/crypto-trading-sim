package com.cryptotrading.controller;

    import com.cryptotrading.model.Asset;
    import com.cryptotrading.model.UserData;
    import com.cryptotrading.service.UserDataService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users-data")
public class UserDataController {
    private final UserDataService userDataService;

    @Autowired
    public UserDataController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @GetMapping("/{userId}")
    public UserData getUserData(@PathVariable String userId) {
        return userDataService.getUserData(userId);
    }
    @GetMapping("/{userId}/assets/{asset}")
    public Asset getUserAsset(@PathVariable String userId, @PathVariable String asset) {
        return userDataService.getUserAsset(userId, asset);
    }

    @GetMapping("/{userId}/balance")
    public double getUserBalance(@PathVariable String userId) {
        return userDataService.getUserBalance(userId);
    }

//    @GetMapping("/{userId}/transactions")
//    public List<Transaction> getUserTransactions(@PathVariable String userId) {
//        return userDataService.getUserTransactions(userId);
//    }
}