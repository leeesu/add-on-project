package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

/*
* 토큰 만료시간을 관리하는 enum
 */
public enum ExpireEnum {
    //ACCESS : 30분, REFRESH : 7일
    ACCESS_EXPIRE(1000 * 60 * 60 * 24 * 7),
    REFRESH_EXPIRE(1000 * 60 * 60 * 24 * 7);


    private final long time;
}
