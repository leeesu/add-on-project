package com.onpurple.controller;


import com.onpurple.dto.response.ResponseDto;
import com.onpurple.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @DeleteMapping( "/admin/post/{postId}")
    public ResponseDto<?> deletePost(@PathVariable Long postId, HttpServletRequest request) {

        return adminService.deletePostByAdmin(request, postId);
    }

    @DeleteMapping( "/admin/comment/{commentId}")
    public ResponseDto<?> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {

        return adminService.deleteCommentByAdmin(request, commentId);
    }

}