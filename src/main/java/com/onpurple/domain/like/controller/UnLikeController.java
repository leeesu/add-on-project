package com.onpurple.domain.like.controller;

import com.onpurple.domain.like.dto.UnLikesResponseDto;
import com.onpurple.domain.like.service.UnLikeService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UnLikeController {

    private final UnLikeService unLikeService;


    // 회원 싫어요
    @PostMapping( "/user/unlike/{targetId}")
    public ApiResponseDto<MessageResponseDto> createUserUnLike(@PathVariable final Long targetId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return unLikeService.userUnLike(targetId, userDetails.getUser());
    }

    @GetMapping("/user/unLike")
    public ApiResponseDto<List<UnLikesResponseDto>> getUnLikeList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return unLikeService.getUnLike(userDetails.getUser());
    }
}
