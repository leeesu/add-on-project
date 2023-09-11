package com.onpurple.config.handler;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp 접속 주소 url => /ws/chat
        registry.addEndpoint("/ws/chat").setAllowedOrigins("*") // 연결될 엔드포인트
                .withSockJS(); // SocketJS 를 연결한다는 설정
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독 요청 url => 메시지 받을 때
        registry.enableSimpleBroker("/sub");
        // 메시지를 발행 요청 url => 메시지 보낼 때
        registry.setApplicationDestinationPrefixes("/pub");
    }
}