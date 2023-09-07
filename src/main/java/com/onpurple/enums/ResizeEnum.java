package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResizeEnum {
    IMG_WIDTH(428),
    IMG_HEIGHT(428);

    private final int size;
}
