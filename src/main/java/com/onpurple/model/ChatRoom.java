package com.onpurple.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


// Stomp 를 통해 pub/sub 를 사용하면 구독자 관리가 알아서 된다!!
// 따라서 따로 세션 관리를 하는 코드를 작성할 필도 없고,
// 메시지를 다른 세션의 클라이언트에게 발송하는 것도 구현 필요가 없다!
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "roomId", nullable = false)
    private Long id; // 채팅방 아이디

    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "otherUserId")
    @ManyToOne(fetch = FetchType.LAZY)
    private User otherUser;

    private String userProfile;

    private String otherUserProfile;

}