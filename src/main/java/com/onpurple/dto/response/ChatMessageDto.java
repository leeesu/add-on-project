package com.onpurple.dto.response;

import com.onpurple.model.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {
    private String sender;
    private String roomId;
    private String message;
    private String sentTime;
    private String receiver;

    // 대화 조회
    public ChatMessageDto(ChatMessage chatMessage) {
        this.sender = chatMessage.getSender();
        this.roomId = chatMessage.getRoomId();
        this.message = chatMessage.getMessage();
        this.receiver = chatMessage.getReceiver();
    }
}