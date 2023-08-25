package com.project.date.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * String roomName
 * String roomId
 * String lastMessage
 * LocalDateTime createAt
 * int unreadCount
 * String otherUserImage
 * int totalCnt
 */
@NoArgsConstructor
@Getter
@Setter
public class ChatRoomResponseDto implements Comparable<ChatRoomResponseDto>{
    private String otherUserId;
    private String nickname;
    private String otherNickname;
    private String roomId;
    private String lastMessage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    private int unreadCount;

    private String otherImageUrl;
    private int totalCnt;

    public ChatRoomResponseDto(String roomId, String otherUserId, String myNickname, String otherNickname , String otherImageUrl, String lastMessage, LocalDateTime lastTime, int unReadMessageCount, int totalCnt) {
        this.otherUserId = otherUserId;
        this.roomId = roomId;
        this.otherNickname = otherNickname;
        this.nickname = myNickname;
        this.otherImageUrl = otherImageUrl;
        this.lastMessage = lastMessage;
        this.createAt = lastTime;
        this.unreadCount = unReadMessageCount;
        this.totalCnt = totalCnt;
    }

    @Override
    public int compareTo(ChatRoomResponseDto responseDto) {
        if (responseDto.createAt.isBefore(createAt)) {
            return 1;
        } else if (responseDto.createAt.isAfter(createAt)) {
            return -1;
        }
        return 0;
    }

}
