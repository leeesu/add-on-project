package com.onpurple.global.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class KakaoUserRequestDto {

    private Long id;
    private String nickname;
    private String imageUrl;

}
