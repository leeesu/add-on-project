package com.onpurple.global.kakao.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoLoginResponseDto {
    private String nickname;
    private String imageUrl;

}
