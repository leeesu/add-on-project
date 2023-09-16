package com.onpurple.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onpurple.model.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageResponseDto {
    private Long id;
    private String roomName;
    private String sender;
    private String roomId;
    private String receiver;
    private String message;
    private String createdAt;

    // 쪽지방 생성
    public ChatMessageResponseDto(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.roomName = chatRoom.getRoomName();
        this.sender = chatRoom.getSender();
        this.roomId = chatRoom.getRoomId();
        this.receiver = chatRoom.getReceiver();
    }

    // 사용자 관련 쪽지방 전체 조회
    public ChatMessageResponseDto(Long id, String roomName, String roomId, String sender, String receiver) {
        this.id = id;
        this.roomName = roomName;
        this.roomId = roomId;
        this.sender = sender;
        this.receiver = receiver;
    }

    public ChatMessageResponseDto(String roomId) {
        this.roomId = roomId;
    }

    public void setLatestChatMessageContent(String message) {
        this.message = message;
    }

    public void setLatestChatMessageCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}