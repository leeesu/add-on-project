package com.onpurple.domain.admin.controller;



import com.onpurple.domain.admin.service.AdminService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
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
    public ApiResponseDto<?> deletePost(@PathVariable final Long postId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return adminService.deletePostByAdmin(userDetails.getUser(), postId);
    }

    @DeleteMapping( "/admin/comment/{commentId}")
    public ApiResponseDto<?> deleteComment(@PathVariable final Long commentId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return adminService.deleteCommentByAdmin(userDetails.getUser(), commentId);
    }

}