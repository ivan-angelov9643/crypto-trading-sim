package com.cryptotrading.handler;

import com.cryptotrading.event.PriceUpdateEvent;
import com.cryptotrading.service.KrakenWebSocketClient;
import com.cryptotrading.session.SessionManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class CryptoWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private KrakenWebSocketClient krakenWebSocketClient;
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)  {
        String payload = message.getPayload();
        System.out.println("Received from frontend: " + payload);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionManager.addSession(session);
        eventPublisher.publishEvent(new PriceUpdateEvent(krakenWebSocketClient.getCryptoPrices()));
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