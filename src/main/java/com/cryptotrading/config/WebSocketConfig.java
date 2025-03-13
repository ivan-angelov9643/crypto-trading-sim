package com.cryptotrading.config;

import com.cryptotrading.handler.CryptoWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CryptoWebSocketHandler cryptoWebSocketHandler;

    @Autowired
    public WebSocketConfig(CryptoWebSocketHandler cryptoWebSocketHandler) {
        this.cryptoWebSocketHandler = cryptoWebSocketHandler;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(cryptoWebSocketHandler, "/crypto-prices")
            .setAllowedOrigins("*");
    }
}