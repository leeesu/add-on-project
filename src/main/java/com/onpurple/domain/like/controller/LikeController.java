package com.onpurple.domain.like.controller;

import com.onpurple.domain.like.dto.LikeResponseDto;
import com.onpurple.domain.like.dto.LikesResponseDto;
import com.onpurple.domain.like.service.LikeService;
import com.onpurple.domain.unLike.service.UnLikeService;
import com.onpurple.domain.user.dto.UserResponseDto;
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

@RequiredArgsConstructor
@RestController
public class LikeController {

    private final LikeService likeService;
    private final UnLikeService unLikeService;


    // 게시글 좋아요
    @PostMapping( "/post/like/{postId}")
    public ApiResponseDto<LikeResponseDto> createPostLike(@PathVariable final Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.postLike(postId, userDetails.getUser());
    }
    // 댓글 좋아요
    @PostMapping( "/comment/like/{commentId}")
    public ApiResponseDto<LikeResponseDto> createCommentLike(@PathVariable final Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.commentLike(commentId, userDetails.getUser());
    }

    // 회원 좋아요
    @PostMapping( "/user/like/{targetId}")
    public ApiResponseDto<MessageResponseDto> createUserLike(@PathVariable final Long targetId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.userLike(targetId, userDetails.getUser());
    }


    @PostMapping( "/user/match/{userId}")
    public ApiResponseDto<List<UserResponseDto>> createCheckUser(@PathVariable final Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.likeCheck(userId, userDetails.getUser());
    }
    @GetMapping("/user/like")
    public ApiResponseDto<List<LikesResponseDto>> getLikeList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return likeService.getLike(userDetails.getUser());
    }

}
