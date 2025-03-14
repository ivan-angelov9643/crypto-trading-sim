package com.cryptotrading.config;

import com.cryptotrading.handler.CryptoWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    private final CryptoWebSocketHandler cryptoWebSocketHandler;

    @Autowired
    public WebSocketConfig(CryptoWebSocketHandler cryptoWebSocketHandler) {
        this.cryptoWebSocketHandler = cryptoWebSocketHandler;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        logger.info("Registering WebSocket handler at /crypto-prices");

        registry.addHandler(cryptoWebSocketHandler, "/crypto-prices")
            .setAllowedOrigins("https://localhost:8080");

        logger.info("WebSocket handler registered successfully.");
    }
}