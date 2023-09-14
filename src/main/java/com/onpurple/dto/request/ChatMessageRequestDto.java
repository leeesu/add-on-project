package com.onpurple.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessageRequestDto {

    private Long senderId;
    private String message;

}