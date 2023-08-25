package com.project.date.controller;

import com.project.date.dto.response.ResponseDto;
import com.project.date.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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