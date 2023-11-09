package com.onpurple.domain.user.dto;

import com.onpurple.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OtherLikeResponseDto {

    private Long userId;

    private String imageUrl;

    public static OtherLikeResponseDto fromEntity(User user) {
        return OtherLikeResponseDto.builder()
                .userId(user.getId())
                .imageUrl(user.getImageUrl())
                .build();
    }

}
