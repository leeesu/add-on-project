package com.project.date.dto.response;

import com.project.date.dto.request.ChatMessageDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Long otherMemberId
 * int unreadCount
 * String roomId
 * String type
 */
@Getter
@Setter
public class UnreadMessageCount {

    private Long otherUserId;
    private int unreadCount;
    private String roomId;
    private String type;

    public UnreadMessageCount(ChatMessageDto roomMessage) {
        this.type = "UNREAD";
        this.otherUserId = roomMessage.getOtherUserId();
        this.unreadCount = roomMessage.getCount();
        this.roomId = roomMessage.getRoomId();
    }
}
