package com.onpurple.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResizeConstants {
    IMG_WIDTH(428),
    IMG_HEIGHT(428);

    private final int size;
}
