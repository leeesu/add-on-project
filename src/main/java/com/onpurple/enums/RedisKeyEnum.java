package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
* Redis Key를 관리하는 enum
 */
@Getter
@AllArgsConstructor
public enum RedisKeyEnum {

    REFRESH_TOKEN_KEY("refreshToken:"),


    ;

    private final String desc;

}
