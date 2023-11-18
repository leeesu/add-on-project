package com.onpurple.domain.like.controller;

import com.onpurple.domain.like.dto.LikeResponseDto;
import com.onpurple.domain.like.dto.LikesResponseDto;
import com.onpurple.domain.like.service.LikeService;
import com.onpurple.domain.like.service.UnLikeService;
import com.onpurple.domain.user.dto.UserResponseDto;
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

@RequiredArgsConstructor
@RestController
@Tag(name = "좋아요 API", description = "게시글-댓글-회원 좋아요 생성, 취소")
public class LikeController {

    private final LikeService likeService;


    // 게시글 좋아요
    @PostMapping( "/post/like/{postId}")
    @Operation(summary = "게시글 좋아요", description = "게시글 좋아요")
    @Parameter(name = "postId", description = "좋아요를 누를 게시글의 id", required = true)
    @Parameter(name = "userDetails", description = "좋아요를 누를 사용자의 정보", required = true)
    public ApiResponseDto<LikeResponseDto> createPostLike(@PathVariable final Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.postLike(postId, userDetails.getUser());
    }
    // 댓글 좋아요
    @PostMapping( "/comment/like/{commentId}")
    @Operation(summary = "댓글 좋아요", description = "댓글 좋아요")
    @Parameter(name = "commentId", description = "좋아요를 누를 댓글의 id", required = true)
    @Parameter(name = "userDetails", description = "좋아요를 누를 사용자의 정보", required = true)
    public ApiResponseDto<LikeResponseDto> createCommentLike(@PathVariable final Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.commentLike(commentId, userDetails.getUser());
    }

    // 회원 좋아요
    @PostMapping( "/user/like/{targetId}")
    @Operation(summary = "회원 좋아요", description = "회원 좋아요")
    @Parameter(name = "targetId", description = "좋아요를 누를 회원의 id", required = true)
    @Parameter(name = "userDetails", description = "좋아요를 누를 사용자의 정보", required = true)
    public ApiResponseDto<MessageResponseDto> createUserLike(@PathVariable final Long targetId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.userLike(targetId, userDetails.getUser());
    }


    @PostMapping( "/user/match/{userId}")
    @Operation(summary = "회원 매칭", description = "서로 좋아요한 회원 매칭")
    @Parameter(name = "userDetails", description = "매칭 확인할 사용자의 정보", required = true)
    public ApiResponseDto<List<UserResponseDto>> createCheckUser(@PathVariable final Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.likeCheck(userDetails.getUser());
    }
    @GetMapping("/user/like")
    @Operation(summary = "좋아요한 회원 리스트 조회", description = "나를 좋아요한 회원 리스트 조회")
    @Parameter(name = "userDetails", description = "좋아요한 회원 리스트를 조회할 사용자의 정보", required = true)
    public ApiResponseDto<List<LikesResponseDto>> getLikeList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return likeService.getLike(userDetails.getUser());
    }

}
