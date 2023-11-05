package com.onpurple.global.redis.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onpurple.domain.chatMessage.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {		// 1.
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    // 2. Redis 에서 메시지가 발행(publish)되면, listener 가 해당 메시지를 읽어서 처리
    @Override
    public void onMessage(Message message, byte[] pattern) {		// 3.
        try {
            // redis 에서 발행된 데이터를 받아 역직렬화
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            // 4. 해당 객체를 MessageDto 객체로 맵핑
            ChatMessageDto roomMessage = objectMapper.readValue(publishMessage, ChatMessageDto.class);

            // 5. Websocket 구독자에게 채팅 메시지 전송
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getRoomId(), roomMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}