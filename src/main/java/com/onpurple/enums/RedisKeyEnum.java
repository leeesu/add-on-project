package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
* Redis Key를 관리하는 enum
 */
@Getter
@AllArgsConstructor
public enum RedisKeyEnum {

    REFRESH_TOKEN_KEY("RefreshToken:"),


    // Cache Key
    LOGIN_USER_KEY("User:"),
    USER_LIST_KEY("UserList:"),
    ;

    private final String desc;

}

