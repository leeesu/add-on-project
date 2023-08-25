package com.project.date.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class ChatRoomUser extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    private String nickname;
    private String name;
    private String otherImageUrl;
    private String myNickname;
    private String otherNickname;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    public ChatRoomUser(User user, User otherUser, ChatRoom room) {
        this.user = user;
        this.otherNickname = otherUser.getNickname();
        this.name = otherUser.getUsername();
        this.chatRoom = room;
        this.myNickname = user.getNickname();
        this.otherImageUrl = otherUser.getImageUrl();
    }
}
