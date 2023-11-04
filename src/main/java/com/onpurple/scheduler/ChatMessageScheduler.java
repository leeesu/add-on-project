package com.onpurple.scheduler;

import com.onpurple.enums.RedisKeyEnum;
import com.onpurple.model.ChatMessage;
import com.onpurple.repository.ChatMessageRepository;
import com.onpurple.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Set;

import static com.onpurple.enums.RedisKeyEnum.*;

@RequiredArgsConstructor
public class ChatMessageScheduler {
    private final RedisTemplate<String, ChatMessage> redisTemplateMessage;
    private final ChatMessageRepository chatMessageRepository;

    private static final int MAX_MESSAGE_COUNT = 100;


    // 매일 AM 12시, PM 12시에 실행되는 스케줄링 작업
    @Scheduled(cron = "0 0 0,12 * * ?")
    public void saveMessagesToDb() {
        // 모든 방의 ID를 가져옵니다.
        Set<String> roomIds = redisTemplateMessage.keys("*");

        for (String roomId : roomIds) {
            Long size = redisTemplateMessage.opsForList().size(ChatRoom_KEY+roomId);
            List<ChatMessage> messages =
                    redisTemplateMessage.opsForList().range(ChatRoom_KEY+roomId, 0, size - 1);
            chatMessageRepository.saveAll(messages);
            // 최근 100개 메시지만 남김
            redisTemplateMessage.opsForList().trim(ChatRoom_KEY+roomId, size - 100, size - 1);
        }
    }
}
