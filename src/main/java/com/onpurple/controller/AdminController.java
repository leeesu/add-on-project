package com.onpurple.controller;


import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @DeleteMapping( "/admin/post/{postId}")
    public ApiResponseDto<?> deletePost(@PathVariable Long postId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return adminService.deletePostByAdmin(userDetails.getUser(), postId);
    }

    @DeleteMapping( "/admin/comment/{commentId}")
    public ApiResponseDto<?> deleteComment(@PathVariable Long commentId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return adminService.deleteCommentByAdmin(userDetails.getUser(), commentId);
    }

}