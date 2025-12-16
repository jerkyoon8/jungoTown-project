package com.juwon.springcommunity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // 클라이언트에서 웹소켓에 접속할 수 있는 주소(Endpoint)를 지정합니다.
        registry.addEndpoint("/ws-stomp").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // "/topic"이나 "/queue"로 시작하는 주소를 구독하는 클라이언트에게 메시지를 전달합니다.
        registry.enableSimpleBroker("/topic", "/queue");

        // "/app"으로 시작하는 주소로 메시지를 보내면, 해당 메시지는 @MessageMapping 어노테이션이 붙은 메소드로 라우팅됩니다.
        registry.setApplicationDestinationPrefixes("/app");
    }
}
