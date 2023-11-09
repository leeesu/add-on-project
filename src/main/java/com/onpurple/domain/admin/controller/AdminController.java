package com.onpurple.domain.admin.controller;



import com.onpurple.domain.admin.service.AdminService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 API", description = "관리자 권한으로만 접근 가능한 API")
@RequiredArgsConstructor
@RestController
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "관리자 권한으로 게시글 삭제", description = "관리자 권한으로 게시글 삭제")
    @Parameter(name = "postId", description = "삭제할 게시글의 id", required = true)
    @Parameter(name = "userDetails", description = "관리자 권한을 가진 사용자의 정보", required = true)
    @DeleteMapping( "/admin/post/{postId}")
    public ApiResponseDto<?> deletePost(@PathVariable final Long postId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return adminService.deletePostByAdmin(userDetails.getUser(), postId);
    }

    @Operation(summary = "관리자 권한으로 댓글 삭제", description = "관리자 권한으로 댓글 삭제")
    @Parameter(name = "commentId", description = "삭제할 댓글의 id", required = true)
    @Parameter(name = "userDetails", description = "관리자 권한을 가진 사용자의 정보", required = true)
    @DeleteMapping( "/admin/comment/{commentId}")
    public ApiResponseDto<?> deleteComment(@PathVariable final Long commentId,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return adminService.deleteCommentByAdmin(userDetails.getUser(), commentId);
    }

}