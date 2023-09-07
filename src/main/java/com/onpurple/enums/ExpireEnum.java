package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

public enum ExpireEnum {
    //ACCESS : 30분, REFRESH : 7일
    ACCESS_EXPIRE(60000),
    REFRESH_EXPIRE(1000 * 60 * 60 * 24 * 7);


    private final long time;
}
