package com.cryptotrading;

import com.cryptotrading.service.KrakenWebSocketClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CryptoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CryptoApplication.class, args);

        KrakenWebSocketClient krakenClient = context.getBean(KrakenWebSocketClient.class);
        krakenClient.connect();
    }
}