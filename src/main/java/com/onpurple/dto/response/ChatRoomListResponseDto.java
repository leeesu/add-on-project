package com.project.date.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * List<ChatRoomResponseDto> ChatRoomResponseDto
 * int totalCnt
 */
@NoArgsConstructor
@Setter
@Getter
public class ChatRoomListResponseDto {
    private List<ChatRoomResponseDto> chatRoomResponseDto;
    private int totalCnt;

    public ChatRoomListResponseDto(List<ChatRoomResponseDto> chatRoomResponseDto, int totalCnt) {
        this.chatRoomResponseDto = chatRoomResponseDto;
        this.totalCnt = totalCnt;
    }
}
