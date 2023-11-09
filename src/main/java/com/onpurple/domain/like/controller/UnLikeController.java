package com.onpurple.domain.like.controller;

import com.onpurple.domain.like.dto.UnLikesResponseDto;
import com.onpurple.domain.like.service.UnLikeService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "싫어요 API", description = "회원 싫어요 생성, 취소")
public class UnLikeController {

    private final UnLikeService unLikeService;


    // 회원 싫어요
    @PostMapping( "/user/unlike/{targetId}")
    @Operation(summary = "회원 싫어요", description = "회원 싫어요")
    @Parameter(name = "targetId", description = "싫어요를 누를 회원의 id", required = true)
    @Parameter(name = "userDetails", description = "싫어요를 누를 사용자의 정보", required = true)
    public ApiResponseDto<MessageResponseDto> createUserUnLike(@PathVariable final Long targetId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return unLikeService.userUnLike(targetId, userDetails.getUser());
    }

    @GetMapping("/user/unLike")
    @Operation(summary = "회원 싫어요 조회", description = "회원 싫어요 조회")
    @Parameter(name = "userDetails", description = "싫어요를 조회할 사용자의 정보", required = true)
    public ApiResponseDto<List<UnLikesResponseDto>> getUnLikeList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return unLikeService.getUnLike(userDetails.getUser());
    }
}
