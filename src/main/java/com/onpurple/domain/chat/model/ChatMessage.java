package com.onpurple.domain.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onpurple.global.dto.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "chatMessage")
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender")
    private String sender;

    @Column(name = "roomId")
    private String roomId;

    @Column(name = "receiver")
    private String receiver;

    @Column(name = "message")
    private String message;

    @Column(name = "sentTime")
    private String sentTime;

    // 1.
    @ManyToOne
    @JoinColumn(name = "roomId", referencedColumnName = "roomId", insertable = false, updatable = false)
    private ChatRoom chatRoom;

    // 대화 저장
    public ChatMessage(String sender, String receiver, String roomId, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.roomId = roomId;
        this.message = message;
    }
}  