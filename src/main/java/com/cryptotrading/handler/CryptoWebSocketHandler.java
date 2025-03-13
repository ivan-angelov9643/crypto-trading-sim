package com.cryptotrading.handler;

import com.cryptotrading.event.PriceUpdateEvent;
import com.cryptotrading.service.KrakenWebSocketClient;
import com.cryptotrading.session.SessionManager;
import com.google.gson.Gson;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Component
public class CryptoWebSocketHandler extends TextWebSocketHandler {
    private final SessionManager sessionManager;
    private final KrakenWebSocketClient krakenWebSocketClient;
    private final Gson gson = new Gson();

    @Autowired
    public CryptoWebSocketHandler(SessionManager sessionManager, KrakenWebSocketClient krakenWebSocketClient) {
        this.sessionManager = sessionManager;
        this.krakenWebSocketClient = krakenWebSocketClient;
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)  {
        String payload = message.getPayload();
        System.out.println("Received from frontend: " + payload);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        sessionManager.addSession(session);

        String jsonPrices = gson.toJson(krakenWebSocketClient.getCryptoPrices());
        session.sendMessage(new TextMessage(jsonPrices));

        System.out.println("New WebSocket connection established: " + session.getId());
        System.out.println("Number of active sessions: " + sessionManager.getSessions().size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessionManager.removeSession(session);
        System.out.println("WebSocket connection closed: " + session.getId());
        System.out.println("Number of active sessions: " + sessionManager.getSessions().size());
    }
}