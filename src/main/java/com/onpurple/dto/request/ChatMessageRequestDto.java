package com.onpurple.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageRequestDto {

    private Long senderId;
    private String message;
}