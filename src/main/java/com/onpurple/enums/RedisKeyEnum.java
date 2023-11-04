package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
* Redis Key를 관리하는 enum
 */
@Getter
@AllArgsConstructor
public enum RedisKeyEnum {
    /**
     * Redis Key
     */

    // User 리프레시 토큰 관리 키
    REFRESH_TOKEN_KEY("RefreshToken:"),

    // Chat 메세지 저장을 위한 ChatRoom 관리 키
    ChatRoom_KEY("ChatRoom:"),


    /**
     * Cache Key
     */
    // User 로그인 관리 키
    LOGIN_USER_KEY("User:"),
    // User 리스트 관리 키
    USER_LIST_KEY("UserList:"),
    ;

    private final String desc;

}

