package com.onpurple.domain.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

    COMMENT("댓글"),
    COMMENT_LIKE("댓글 좋아요"),
    POST_LIKE("게시글 좋아요"),
    LIKE_ME("나를 좋아요"),
    USER_MATCH("매칭"),
    ;


    private final String message;
}
