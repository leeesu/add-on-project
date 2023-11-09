package com.onpurple.domain.chat.controller;

import com.onpurple.domain.chat.dto.ChatMessageDto;
import com.onpurple.domain.chat.service.ChatMessageService;
import com.onpurple.domain.chat.service.ChatRoomService;
import com.onpurple.global.redis.pubsub.RedisPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "채팅 메세지 API", description = "채팅 메시지 전송, 채팅 메시지 조회")
public class ChatMessageController {
    private final RedisPublisher redisPublisher; // Redis 메시지 발행을 위한 객체
    private final ChatRoomService chatRoomService; // 채팅방 서비스 객체
    private final ChatMessageService chatMessageService; // 채팅 메시지 서비스 객체

    // 대화 & 대화 저장
    @MessageMapping("/message")     // 1. 클라이언트에서 "/message"로 보낸 메시지를 처리하는 메소드
    @Operation(summary = "채팅 메시지 전송", description = "채팅 메시지를 전송, DB와 Redis에 저장")
    @Parameter(name = "chatMessageDto", description = "채팅 메시지 정보", required = true)
    public void message(ChatMessageDto chatMessageDto) {
        // 클라이언트의 채팅방(topic) 입장, 대화를 위해 리스너와 연동
        chatRoomService.enterMessageRoom(chatMessageDto.getRoomId()); // 채팅방에 입장

        // Websocket 에 발행된 메시지를 redis 로 발행. 해당 쪽지방을 구독한 클라이언트에게 메시지가 실시간 전송됨 (1:N, 1:1 에서 사용 가능)
        redisPublisher.publish(chatRoomService.getTopic(chatMessageDto.getRoomId()), chatMessageDto); // 메시지를 Redis에 발행

        // DB & Redis 에 대화 저장
        chatMessageService.saveMessage(chatMessageDto); // 메시지를 DB와 Redis에 저장
    }

    // 대화 내역 조회
    @GetMapping("/chat/room/{roomId}/message") // 2. "/api/room/{roomId}/message" 경로로 GET 요청을 보낼 때 처리하는 메소드
    @Operation(summary = "채팅 메시지 조회", description = "특정 채팅방의 메시지 목록을 가져옴")
    @Parameter(name = "roomId", description = "채팅방의 id", required = true)
    public ResponseEntity<List<ChatMessageDto>> loadMessage(@PathVariable String roomId) { // 특정 채팅방의 메시지 목록을 가져옴
        return ResponseEntity.ok(chatMessageService.loadMessage(roomId)); // 해당 채팅방의 모든 메시지를 반환
    }
}
