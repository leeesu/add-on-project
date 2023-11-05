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
public class LikedResponseDto {

    private Long userId;

    private String imageUrl;

    public static LikedResponseDto fromEntity(Likes like) {
        return LikedResponseDto.builder()
                .userId(like.getUser().getId())
                .imageUrl(like.getUser().getImageUrl())
                .build();
    }

}
