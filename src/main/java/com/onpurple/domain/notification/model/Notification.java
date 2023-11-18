package com.onpurple.domain.notification.model;

import com.onpurple.domain.notification.enums.NotificationType;
import com.onpurple.domain.user.model.User;
import com.onpurple.global.dto.Timestamped;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity(name = "notification")
@EqualsAndHashCode(of = "id")
@Setter
@Getter //각 래퍼 클래스에 대한 추출 메소드이다.
@NoArgsConstructor
public class Notification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //누구 : ~에 대한 알림이 도착했습니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;
    @Column
    private String senderUsername;
    @Column
    private String senderNickname;
    @Column
    private String senderProfileImageUrl;

    @Column
    private Boolean isRead = false; // 읽음 표시

    @Column(nullable = false)
    private String message;  // 알림 메시지

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType; // 알림 타입


    @Builder
    public Notification(
            User receiver, Boolean readState,
            String message, NotificationType notificationType,
            String senderUsername,String senderNickname,
            String senderProfileImageUrl) {

        this.receiver = receiver;
        this.isRead = readState;
        this.message = message;
        this.notificationType = notificationType;
        this.senderUsername = senderUsername;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
    }
    // 알림 읽음
    public void confirm(){
        this.isRead = true;
    }



}