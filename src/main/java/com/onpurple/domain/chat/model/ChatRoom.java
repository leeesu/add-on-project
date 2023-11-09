package com.onpurple.domain.chat.model;

import com.onpurple.domain.chat.model.ChatMessage;
import com.onpurple.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "chatRoom")
@NoArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    private String sender;			// 채팅방 생성자

    @Column(unique = true)
    private String roomId;

    private String receiver;        // 채팅방 수신자

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatMessage> ChatMessageList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // 쪽지방 생성
    public ChatRoom(Long id, String roomName, String sender, String roomId, String receiver, User user) {
        this.id = id;
        this.roomName = roomName;
        this.sender = sender;
        this.roomId = roomId;
        this.receiver = receiver;
        this.user = user;

    }
}