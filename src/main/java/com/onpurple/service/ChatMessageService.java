package com.onpurple.service;

import com.onpurple.dto.response.ChatMessageDto;
import com.onpurple.model.ChatMessage;
import com.onpurple.repository.ChatMessageRepository;
import com.onpurple.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final RedisTemplate<String, ChatMessageDto> redisTemplateMessage;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;


    /**
     * 대화 저장
     * ChatMessageDto 를 ChatMessage 로 변환
     * @param messageDto
     * @return void
     */
    public void saveMessage(ChatMessageDto messageDto) {
        // DB 저장
        ChatMessage chatMessage = new ChatMessage(messageDto.getSender(), messageDto.getRoomId(), messageDto.getMessage());
        chatMessageRepository.save(chatMessage);

        // 1. 직렬화
        redisTemplateMessage.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));

        // 2. redis 저장
        redisTemplateMessage.opsForList().rightPush(messageDto.getRoomId(), messageDto);

        // 3. expire 을 이용해서, Key 를 만료시킬 수 있음
        redisTemplateMessage.expire(messageDto.getRoomId(), 1, TimeUnit.MINUTES);
    }

    /**
     * 대화 조회 - Redis & DB
     * @param roomId
     * @return List<ChatMessageDto>
     */
    public List<ChatMessageDto> loadMessage(String roomId) {
        List<ChatMessageDto> messageList = new ArrayList<>();

        // Redis 에서 해당 채팅방의 메시지 100개 가져오기
        List<ChatMessageDto> redisMessageList = redisTemplateMessage.opsForList().range(roomId, 0, 99);

        // 4. Redis 에서 가져온 메시지가 없다면, DB 에서 메시지 100개 가져오기
        if (redisMessageList == null || redisMessageList.isEmpty()) {
            // 5.
            List<ChatMessage> dbMessageList = chatMessageRepository.findTop100ByRoomIdOrderByCreatedAtAsc(roomId);

            for (ChatMessage message : dbMessageList) {
                ChatMessageDto messageDto = new ChatMessageDto(message);
                messageList.add(messageDto);
                redisTemplateMessage.setValueSerializer(new Jackson2JsonRedisSerializer<>(ChatMessage.class));      // 직렬화
                redisTemplateMessage.opsForList().rightPush(roomId, messageDto);                                // redis 저장
            }
        } else {
            // 7.
            messageList.addAll(redisMessageList);
        }

        return messageList;
    }
}