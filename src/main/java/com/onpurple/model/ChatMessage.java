package com.project.date.model;

import com.project.date.dto.request.ChatMessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ChatMessage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 메세지 작성자
    @ManyToOne
    private User user;
    private String otherNickname;
    private String otherUserId;
    private String otherImageUrl;
    private String roomId;

    // 채팅 메세지 내용
    @Size(max = 1000)
    private String message;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    public ChatMessage(User user, ChatMessageDto chatMessageDto, ChatRoom chatRoom) {

        this.user = user;
        this.message = chatMessageDto.getMessage();
        this.chatRoom = chatRoom;
        this.roomId = chatRoom.getChatRoomUuid();
    }
}