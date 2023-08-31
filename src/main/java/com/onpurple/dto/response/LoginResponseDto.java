package com.onpurple.dto.response;

import com.onpurple.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String nickname;
    private String imageUrl;


    public static LoginResponseDto fromEntity(@NotNull User user) {
        return LoginResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
