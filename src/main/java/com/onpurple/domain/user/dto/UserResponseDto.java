package com.onpurple.domain.user.dto;

import com.onpurple.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long userId;

    private String nickname;

    private String gender;

    private String imageUrl;

    private List<String> imgList;

    private String role;

    public static UserResponseDto createFromEntity(User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .gender(user.getGender())
                .build();
    }

    public static UserResponseDto getFromEntity(User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .gender((user.getGender()))
                .imageUrl(user.getImageUrl())
                .role(String.valueOf(user.getRole()))
                .build();
    }

}
