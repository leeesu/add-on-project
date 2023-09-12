package com.onpurple.dto.response;
import com.onpurple.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessageResponseDto {

    private Long chatMessageId;
    private Long senderId;
    private String profileImage;
    private String nickname;
    private String message;
    private String createAt;


    public static ChatMessageResponseDto fromEntity(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .chatMessageId(chatMessage.getId())
                .senderId(chatMessage.getSender().getId())
                .profileImage(chatMessage.getSender().getImageUrl())
                .nickname(chatMessage.getSender().getNickname())
                .message(chatMessage.getMessage())
                .createAt(chatMessage.getCreatedAt())
                .build();
    }
}