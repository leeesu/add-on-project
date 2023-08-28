package com.onpurple.controller;

import com.onpurple.dto.request.ReCommentRequestDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.ReCommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReCommentController {

    private final ReCommentService reCommentService;


    // 대댓글 작성
    @PostMapping( "/reComment/{commentId}")
    public ResponseDto<?> createReComment(@PathVariable Long commentId,
                                          @RequestPart(value = "data") ReCommentRequestDto requestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reCommentService.createReComment(commentId, requestDto, userDetails.getUser());
    }

    // 대댓글 조회하기
    @GetMapping("/reComment/{commentId}")
    public ResponseDto<?> getAllReComment(@PathVariable Long commentId) {
            return reCommentService.getAllReCommentsByComment(commentId);
        }


    // 대댓글 수정
    @PutMapping( "/reComment/{reCommentId}")
    public ResponseDto<?> updateReComment(@PathVariable Long reCommentId, ReCommentRequestDto requestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reCommentService.updateReComment(reCommentId, requestDto, userDetails.getUser());
    }

    //대댓글삭제
    @DeleteMapping( "/reComment/{reCommentId}")
    public ResponseDto<?> deleteReComment(@PathVariable Long reCommentId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reCommentService.deleteReComment(reCommentId, userDetails.getUser());
    }
}