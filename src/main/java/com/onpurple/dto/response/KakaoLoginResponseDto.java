package com.onpurple.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoLoginResponseDto {
    private String nickname;
    private String imageUrl;

}
