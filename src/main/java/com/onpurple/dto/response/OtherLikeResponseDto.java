package com.onpurple.dto.response;

import com.onpurple.model.User;
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
