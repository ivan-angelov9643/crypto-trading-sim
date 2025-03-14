package com.cryptotrading.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Validated
@Component
public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public void addSession(@NotNull WebSocketSession session) {
        sessions.add(session);
        logger.info("WebSocket session added: {}", session.getId());
    }

    public void removeSession(@NotNull WebSocketSession session) {
        boolean removed = sessions.remove(session);
        if (removed) {
            logger.info("WebSocket session removed: {}", session.getId());
        } else {
            logger.warn("Attempted to remove non-existent session: {}", session.getId());
            throw new IllegalArgumentException("WebSocket session not found.");
        }
    }

    public List<WebSocketSession> getSessions() {
        return sessions;
    }
}