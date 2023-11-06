package com.onpurple.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onpurple.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomDto implements Serializable {       // Redis 에 저장되는 객체들이 직렬화가 가능하도록

    private static final long serialVersionUID = 6494678977089006639L;      // 역직렬화 위한 serialVersionUID 세팅
    private Long id;
    private String roomName;
    private String roomId;
    private String sender;     // 메시지 송신자
    private String receiver;   // 메시지 수신자

    // 쪽지방 생성
    public static ChatRoomDto create(ChatMessageRequestDto chatMessageRequestDto, User user) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.roomName = chatMessageRequestDto.getReceiver();
        chatRoomDto.roomId = UUID.randomUUID().toString();
        chatRoomDto.sender = user.getNickname();
        chatRoomDto.receiver = chatMessageRequestDto.getReceiver();

        return chatRoomDto;
    }

}