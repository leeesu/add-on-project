package com.onpurple.dto.response;

import com.onpurple.model.ChatMessage;
import com.onpurple.model.ChatRoom;
import com.onpurple.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Builder
@Getter
@AllArgsConstructor
public class ChatRoomResponseDto {
    private Long roomId;
    private Long userId;
    private Long otherUserId;
    private String userProfileImage;
    private String otherUserProfileImage;
    private String userNickname;
    private String otherUserNickname;
    private String recentChatMessage;
    private String recentCreateAt;


    public static ChatRoomResponseDto fromEntity(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .userId(chatRoom.getUser().getId())
                .userProfileImage(chatRoom.getUserProfile())
                .otherUserId(chatRoom.getOtherUser().getId())
                .otherUserProfileImage(chatRoom.getOtherUserProfile())
                .build();
    }

    public static ChatRoomResponseDto ListFromEntity(ChatRoom chatRoom, ChatMessage recentChatMessage) {
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getId())
                .userId(chatRoom.getUser().getId())
                .userNickname(chatRoom.getUser().getNickname())
                .userProfileImage(chatRoom.getUser().getImageUrl())
                .otherUserId(chatRoom.getOtherUser().getId())
                .otherUserNickname(chatRoom.getOtherUser().getNickname())
                .otherUserProfileImage(chatRoom.getOtherUser().getImageUrl())
                .recentChatMessage(recentChatMessage != null ? recentChatMessage.getMessage() : "메세지가 존재하지 않습니다")
                .recentCreateAt(recentChatMessage != null ? recentChatMessage.getCreatedAt() : "메세지가 존재하지 않습니다")
                .build();
    }

}
