package com.goach_backend.goach.config;

import com.goach_backend.goach.logic.sockets.LinkSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final LinkSocketHandler linkSocketHandler;

    public WebSocketConfig(LinkSocketHandler linkSocketHandler) {
        this.linkSocketHandler = linkSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(linkSocketHandler, "/ws/link")
                .setAllowedOrigins("*");
    }
}