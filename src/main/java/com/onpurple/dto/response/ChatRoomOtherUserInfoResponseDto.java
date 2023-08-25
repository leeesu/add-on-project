package com.project.date.dto.response;

import com.project.date.model.User;
import lombok.Getter;
import lombok.Setter;

/**
 * Long otherUserId
 * String otherUsername
 *
 */
@Getter
@Setter
public class ChatRoomOtherUserInfoResponseDto {

    private Long otherUserId;
    private String otherUsername;
    private String otherImageUrl;

    public ChatRoomOtherUserInfoResponseDto(User otherUser){
        this.otherUserId = otherUser.getId();
        this.otherUsername = otherUser.getNickname();
        this.otherImageUrl = otherUser.getImageUrl();
    }
}
