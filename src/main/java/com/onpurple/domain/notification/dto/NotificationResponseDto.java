package com.onpurple.domain.notification.dto;

import com.onpurple.domain.notification.model.Notification;
import com.onpurple.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NotificationResponseDto {

    private Long notificationId;

    private String message;

    private Boolean isRead;

    private NotificationType notificationType;

    private String createdAt;

    private String senderUsername;

    private String senderNickname;

    private String senderProfileImageUrl;



    @Builder
    public NotificationResponseDto(Long id, String message, Boolean isRead,
                                   NotificationType notificationType, String createdAt,
                                   String senderUsername, String senderNickname, String senderProfileImageUrl) {
        this.notificationId = id;
        this.message = message;
        this.isRead = isRead;
        this.notificationType = notificationType;
        this.senderUsername = senderUsername;
        this.senderNickname = senderNickname;
        this.senderProfileImageUrl = senderProfileImageUrl;
        this.createdAt = createdAt;
    }


    public static NotificationResponseDto fromEntity(Notification notification) {

        return NotificationResponseDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .isRead(notification.getIsRead())
                .senderUsername(notification.getSenderUsername())
                .senderNickname(notification.getSenderNickname())
                .senderProfileImageUrl(notification.getSenderProfileImageUrl())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}