package com.onpurple.dto.response;

import com.onpurple.model.Likes;
import com.onpurple.model.User;
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
