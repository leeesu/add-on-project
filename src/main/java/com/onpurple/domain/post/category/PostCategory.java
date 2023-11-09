package com.onpurple.domain.post.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PostCategory {

    TASTE("맛집"),
    COURSE("데이트코스"),
    MEET("번개"),
    BAR("술집"),
    DRIVE("드라이브"),
    FASHION("패선");

    private final String description;

    public static boolean isValidCategory(PostCategory category) {
        for (PostCategory validCategory : values()) {
            if (validCategory.equals(category)) {
                return true;
            }
        }
        return false;
    }

}
