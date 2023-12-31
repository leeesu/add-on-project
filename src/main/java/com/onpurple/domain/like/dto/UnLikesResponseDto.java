package com.onpurple.domain.like.dto;

import com.onpurple.domain.like.model.UnLike;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UnLikesResponseDto{
    private Long userId;
    private String imageUrl;

    public static UnLikesResponseDto fromEntity(UnLike unLike) {
        return UnLikesResponseDto.builder()
                .userId(unLike.getTarget().getId())
                .imageUrl(unLike.getTarget().getImageUrl())
                .build();
    }
}
