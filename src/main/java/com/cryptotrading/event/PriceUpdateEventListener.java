package com.cryptotrading.event;

import com.cryptotrading.model.CryptoPrice;
import com.cryptotrading.session.SessionManager;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Component
public class PriceUpdateEventListener {
    @Autowired
    private SessionManager sessionManager;

    private final Gson gson = new Gson();

    @EventListener
    public void handlePriceUpdateEvent(PriceUpdateEvent event) throws IOException {
//        System.out.println("Handling PriceUpdateEvent");
//        System.out.println("Number of active sessions: " + sessionManager.getSessions().size());
        Map<String, CryptoPrice> prices = event.prices();
        String jsonPrices = gson.toJson(prices);

        for (WebSocketSession session : sessionManager.getSessions()) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(jsonPrices));
//                System.out.println("Sent message to session: " + jsonPrices);
            }
        }
    }
}