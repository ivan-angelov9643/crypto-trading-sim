package com.cryptotrading.handler;

import com.cryptotrading.event.PriceUpdateEvent;
import com.cryptotrading.service.KrakenWebSocketClient;
import com.cryptotrading.session.SessionManager;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Validated
@Component
public class CryptoWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(CryptoWebSocketHandler.class);
    private final SessionManager sessionManager;
    private final KrakenWebSocketClient krakenWebSocketClient;
    private final Gson gson = new Gson();

    @Autowired
    public CryptoWebSocketHandler(SessionManager sessionManager, KrakenWebSocketClient krakenWebSocketClient) {
        this.sessionManager = sessionManager;
        this.krakenWebSocketClient = krakenWebSocketClient;
    }
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message)  {
        String payload = message.getPayload();
        logger.info("Received message from frontend: {}", payload);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        try {
            logger.info("New WebSocket connection established: {}", session.getId());

            sessionManager.addSession(session);
            String jsonPrices = gson.toJson(krakenWebSocketClient.getCryptoPrices());
            session.sendMessage(new TextMessage(jsonPrices));

            logger.info("Number of active sessions: {}", sessionManager.getSessions().size());
        } catch (IOException e) {
            logger.error("Error sending initial crypto prices to session {}: {}", session.getId(), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error in afterConnectionEstablished for session {}: {}", session.getId(), e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        logger.info("WebSocket connection closed: {} with status {}", session.getId(), status);

        sessionManager.removeSession(session);

        logger.info("Number of active sessions: {}", sessionManager.getSessions().size());
    }
}