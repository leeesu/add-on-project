package com.onpurple.domain.like.dto;

import com.onpurple.domain.like.model.Likes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikesResponseDto{

    private Long userId;

    private String imageUrl;

    public static LikesResponseDto fromEntity(Likes likes) {
        return LikesResponseDto.builder()
                .userId(likes.getTarget().getId())
                .imageUrl(likes.getTarget().getImageUrl())
                .build();
    }

}
