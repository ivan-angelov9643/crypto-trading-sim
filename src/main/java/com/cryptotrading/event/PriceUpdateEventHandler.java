package com.cryptotrading.event;

import com.cryptotrading.model.CryptoPrice;
import com.cryptotrading.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Validated
@Component
public class PriceUpdateEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(PriceUpdateEventHandler.class);
    private final SessionManager sessionManager;
    private final Gson gson = new Gson();

    @Autowired
    public PriceUpdateEventHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @EventListener
    public void handlePriceUpdateEvent(@NotNull @Valid PriceUpdateEvent event) {
        CryptoPrice updatedPrice = event.updatedPrice();
        String jsonPrice = gson.toJson(updatedPrice);

        try {
            logger.info("Handling PriceUpdateEvent. Sending updated price: {}", jsonPrice);

            for (WebSocketSession session : sessionManager.getSessions()) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonPrice));
                    logger.info("Sent message to session: {}", session.getId());
                } else {
                    logger.warn("Session {} is not open, skipping.", session.getId());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to send price update message due to an IOException: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred while handling PriceUpdateEvent: {}", e.getMessage());
        }
    }
}