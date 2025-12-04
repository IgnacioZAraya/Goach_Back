package com.goach_backend.goach.logic.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class LinkSocketHandler extends TextWebSocketHandler {

    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        System.out.println("WS Query: " + query);

        if (query == null) {
            session.close(CloseStatus.BAD_DATA.withReason("Missing query params"));
            return;
        }

        Map<String, String> params = new HashMap<>();

        for (String part : query.split("&")) {
            String[] kv = part.split("=");
            if (kv.length == 2) params.put(kv[0], kv[1]);
        }

        String userIdString = params.get("userId");

        if (userIdString == null) {
            session.close(CloseStatus.BAD_DATA.withReason("Missing userId"));
            return;
        }

        try {
            UUID userId = UUID.fromString(userIdString.trim());
            sessions.put(userId, session);
            System.out.println("WS Connected for user: " + userId);
        } catch (Exception e) {
            session.close(CloseStatus.BAD_DATA.withReason("Invalid UUID: " + userIdString));
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.values().removeIf(s -> s.getId().equals(session.getId()));
    }

    public void sendToUser(UUID userId, Object payload) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(payload)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
