package com.onpurple.controller;

import com.onpurple.dto.response.*;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.LikeService;
import com.onpurple.service.UnLikeService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ApiResponseDto<LikeResponseDto> createPostLike(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.PostLike(postId, userDetails.getUser());
    }
    // 댓글 좋아요
    @PostMapping( "/comment/like/{commentId}")
    public ApiResponseDto<LikeResponseDto> createCommentLike(@PathVariable Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.CommentLike(commentId, userDetails.getUser());
    }

    // 회원 좋아요
    @PostMapping( "/user/like/{targetId}")
    public ApiResponseDto<MessageResponseDto> createUserLike(@PathVariable Long targetId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.UserLike(targetId, userDetails.getUser());
    }


    @PostMapping( "/user/match/{userId}")
    public ApiResponseDto<List<UserResponseDto>> createCheckUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.likeCheck(userId, userDetails.getUser());
    }
    @GetMapping("/user/like")
    public ApiResponseDto<List<LikesResponseDto>> getLikeList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return likeService.getLike(userDetails.getUser());
    }

}
