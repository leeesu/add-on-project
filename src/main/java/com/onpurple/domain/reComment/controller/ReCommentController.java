package com.onpurple.domain.reComment.controller;

import com.onpurple.domain.reComment.dto.ReCommentRequestDto;
import com.onpurple.domain.reComment.service.ReCommentService;
import com.onpurple.domain.reComment.dto.ReCommentResponseDto;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReCommentController {

    private final ReCommentService reCommentService;


    // 대댓글 작성
    @PostMapping( "/reComment/{commentId}")
    public ApiResponseDto<ReCommentResponseDto> createReComment(@PathVariable Long commentId,
                                                                @RequestPart(value = "data") final ReCommentRequestDto reCommentRequestDto,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reCommentService.createReComment(commentId, reCommentRequestDto, userDetails.getUser());
    }

    // 대댓글 조회하기
    @GetMapping("/reComment/{commentId}")
    public ApiResponseDto<List<ReCommentResponseDto>> getAllReComment(@PathVariable final Long commentId) {
            return reCommentService.getAllReCommentsByComment(commentId);
        }


    // 대댓글 수정
    @PutMapping( "/reComment/{reCommentId}")
    public ApiResponseDto<ReCommentResponseDto> updateReComment(@PathVariable final Long reCommentId,
                                                                @RequestPart(value = "data") final ReCommentRequestDto reCommentRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reCommentService.updateReComment(reCommentId, reCommentRequestDto, userDetails.getUser());
    }

    //대댓글삭제
    @DeleteMapping( "/reComment/{reCommentId}")
    public ApiResponseDto<MessageResponseDto> deleteReComment(@PathVariable final Long reCommentId,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reCommentService.deleteReComment(reCommentId, userDetails.getUser());
    }
}