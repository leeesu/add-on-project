package com.project.date.dto.request;

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
