package com.onpurple.domain.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {

    COMMENT_MESSAGE("님의 댓글이 등록되었습니다."),
    COMMENT_LIKE_MESSAGE("님의 댓글 좋아요가 등록되었습니다."),
    POST_LIKE_MESSAGE("님의 게시글 좋아요가 등록되었습니다."),
    LIKE_ME_MESSAGE("님이 나를 좋아요 했습니다."),
    USER_MATCH_MESSAGE("님과 회원 매칭에 성공했습니다."),
    ;


    private final String message;
}