package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
* 이미지 리사이징 사이즈를 관리하는 enum
 */
@Getter
@AllArgsConstructor
public enum ResizeEnum {
    IMG_WIDTH(428),
    IMG_HEIGHT(428);

    private final int size;
}
