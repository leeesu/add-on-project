package com.onpurple.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
@Builder
public class UserInfoRequestDto {


    private Integer age;

    private String mbti;

    private String introduction;

    private String idealType;

    private String job;

    private String hobby;

    private String drink;

    private String pet;

    private String smoke;

    private String likeMovieType;

    private String area;

}
