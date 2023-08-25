package com.project.date.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.*;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    private final StompHandler stompHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");            // 메시지를 받을 때 속성, 1 TO 1 :: queue, 1 TO N :: topic
        config.setApplicationDestinationPrefixes("/pub");               // 메시지를 발송할 떄 속성
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp에서 prefix URL 적용하는 부분
        registry.addEndpoint("/stomp/chat") //  Hand-Shake를 맺을 때 사용
                .setAllowedOriginPatterns("*")
                .withSockJS(); // sock.js를 통해서 낮은 버전의 브라우저에서도 websocket이 동작할 수 있게 한다. websocket형태로 연결이 불가능한 경우 http를 사용해서 연결 지속.
    }

}
