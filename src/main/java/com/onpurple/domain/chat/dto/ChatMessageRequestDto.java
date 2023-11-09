package com.onpurple.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequestDto {
    private String receiver;    // 메세지 수신자
}