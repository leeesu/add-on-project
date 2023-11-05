package com.onpurple.global.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

/*
* 토큰 만료시간을 관리하는 enum
 */
public enum ExpireEnum {
    /* RTR기법 적용을 위해 토큰 시간 제한
    * ACCESS : 40분, REFRESH : 1시간
     */
    ACCESS_EXPIRE(1000 * 60 * 40),
    REFRESH_EXPIRE(1000 * 60 * 60);


    private final long time;
}
