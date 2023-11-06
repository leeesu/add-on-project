package com.onpurple.domain.chat.service;

import com.onpurple.domain.chat.dto.ChatMessageDto;
import com.onpurple.domain.chat.model.ChatMessage;
import com.onpurple.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.onpurple.global.enums.RedisKeyEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final RedisTemplate<String, ChatMessage> redisTemplateMessage;
    private final ChatMessageRepository chatMessageRepository;

    private static final int MAX_MESSAGE_COUNT = 100;

    public void saveMessage(ChatMessageDto chatMessageDto) {
        // Redis에 저장
        ChatMessage chatMessage = new ChatMessage(
                chatMessageDto.getSender(),
                chatMessageDto.getReceiver(),
                chatMessageDto.getRoomId(),
                chatMessageDto.getMessage());

        redisTemplateMessage.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));
        redisTemplateMessage.opsForList().rightPush(ChatRoom_KEY+chatMessageDto.getRoomId(), chatMessage);
    }

    public List<ChatMessageDto> loadMessage(String roomId) {
        List<ChatMessageDto> messageList = new ArrayList<>();

        // Redis에서 메시지 가져오기
        List<ChatMessage> redisMessageList = redisTemplateMessage.opsForList().range(ChatRoom_KEY+roomId, 0, MAX_MESSAGE_COUNT - 1);

        if (redisMessageList == null || redisMessageList.isEmpty()) {
            // Redis에서 가져온 메시지가 없다면, DB에서 메시지 가져오기
            List<ChatMessage> dbMessageList = chatMessageRepository.findTop100ByRoomIdOrderByCreatedAtAsc(roomId);
            for (ChatMessage message : dbMessageList) {
                messageList.add(new ChatMessageDto(message));
            }
        } else {
            for (ChatMessage message : redisMessageList) {
                messageList.add(new ChatMessageDto(message));
            }
        }

        return messageList;
    }
}