package com.project.date.controller;


import com.project.date.dto.request.ReCommentRequestDto;
import com.project.date.dto.response.ResponseDto;
import com.project.date.service.ReCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class ReCommentController {

    private final ReCommentService reCommentService;


    // 대댓글 작성
    @PostMapping( "/reComment/{commentId}")
    public ResponseDto<?> createReComment(@PathVariable Long commentId, @RequestPart(value = "data") ReCommentRequestDto requestDto,
                                     HttpServletRequest request) {
        return reCommentService.createReComment(commentId, requestDto,request);
    }

    // 대댓글 조회하기
    @GetMapping("/reComment/{commentId}")
    public ResponseDto<?> getAllReComment(@PathVariable Long commentId) {
            return reCommentService.getAllReCommentsByComment(commentId);
        }


    // 대댓글 수정
    @PutMapping( "/reComment/{reCommentId}")
    public ResponseDto<?> updateReComment(@PathVariable Long reCommentId, ReCommentRequestDto requestDto, HttpServletRequest request) {
        return reCommentService.updateReComment(reCommentId, requestDto, request);
    }

    //대댓글삭제
    @DeleteMapping( "/reComment/{reCommentId}")
    public ResponseDto<?> deleteReComment(@PathVariable Long reCommentId,
                                     HttpServletRequest request) {
        return reCommentService.deleteReComment(reCommentId, request);
    }
}