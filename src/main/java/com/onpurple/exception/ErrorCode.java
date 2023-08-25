package com.onpurple.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // BOARD
    POST_NOT_FOUND("해당 게시글이 존재하지 않습니다"),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),

    //User
    TOKEN_NOT_MATCHED("Token이 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR("서버에 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    PASSWORD_NOT_MATCHED("비밀번호가 일치하지 않습니다."),
    DUPLICATED_USER("중복된 사용자입니다."),
    TOKEN_NOT_FOUND("토큰을 찾을 수 없습니다."),
    TOKEN_ERROR("토큰이 올바르지 않습니다"),
    ACCESS_DENIED("로그인이 필요합니다.");

    private final String message;
}
