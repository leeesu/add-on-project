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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        redisTemplateMessage.opsForList().rightPush(ChatRoom_KEY + chatMessageDto.getRoomId(), chatMessage);
    }

    public List<ChatMessageDto> loadMessage(String roomId) {
        // Redis에서 메시지 가져오기
        List<ChatMessage> redisMessageList = Optional.ofNullable(
                redisTemplateMessage.opsForList().range(
                        ChatRoom_KEY + roomId, 0, MAX_MESSAGE_COUNT - 1))
                .orElse(Collections.emptyList());

        // 만약 Redis에서 메시지를 가져오지 못하거나 비어있다면 DB에서 메시지를 가져옴
        List<ChatMessageDto> messageList = redisMessageList.isEmpty() ?
                chatMessageRepository.findTop100ByRoomIdOrderByCreatedAtAsc(roomId).stream()
                        .map(ChatMessageDto::new)
                        .collect(Collectors.toList()) :
                redisMessageList.stream()
                        .map(ChatMessageDto::new)
                        .collect(Collectors.toList());

        return messageList;
    }
}