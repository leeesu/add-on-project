package com.onpurple.domain.notification.helper;

import com.onpurple.domain.notification.enums.MessageType;
import com.onpurple.domain.notification.enums.NotificationType;
import com.onpurple.domain.notification.service.NotificationService;
import com.onpurple.domain.post.model.Post;
import com.onpurple.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component

public class NotificationRequestManager {
    private final NotificationService notificationService;

    /**
     * 알림 보내기
     * @param receiver 알림을 받을 사람
     * @param sender 알림을 보낸 사람
     * @param type 알림 타입
     * @param messageType 알림 메시지 타입 및 메시지
     */

    private void sendNotification(
            User receiver, User sender, NotificationType type, MessageType messageType) {
        notificationService.send(
                receiver,
                type,
                sender.getUsername() + messageType.getMessage(),
                sender.getUsername(),
                sender.getNickname(),
                sender.getImageUrl());
    }

    public void sendCommentNotification(Post post, User user) {
        sendNotification(
                post.getUser(), user, NotificationType.COMMENT, MessageType.COMMENT_MESSAGE);
    }

    public void sendPostLikeNotification(Post post, User user) {
        sendNotification(
                post.getUser(), user, NotificationType.POST_LIKE, MessageType.POST_LIKE_MESSAGE);
    }

    public void sendCommentLikeNotification(Post post, User user) {
        sendNotification(
                post.getUser(), user, NotificationType.COMMENT_LIKE, MessageType.COMMENT_LIKE_MESSAGE);
    }

    public void sendUserLikeNotification(User target, User user) {
        sendNotification(
                target, user, NotificationType.LIKE_ME, MessageType.LIKE_ME_MESSAGE);
    }
    // 1. 내가 좋아요하고 나한테 매칭 알람이 온다. user, targe순
    // 2. 내가 좋아요해서 상대방한테 매칭 알람이 간다. target, user순
    public void sendUserMatchNotification(User user, User target) {
        // user에게 알람 보내기
        sendNotification(user, target, NotificationType.USER_MATCH, MessageType.USER_MATCH_MESSAGE);

        // target에게도 알람 보내기
        sendNotification(target, user, NotificationType.USER_MATCH, MessageType.USER_MATCH_MESSAGE);
    }

}
