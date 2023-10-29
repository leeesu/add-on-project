package com.onpurple.controller;

import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.LikeResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.UnLikesResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.UnLikeService;
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
