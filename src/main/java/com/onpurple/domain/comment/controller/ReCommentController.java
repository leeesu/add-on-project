package com.onpurple.domain.comment.controller;

import com.onpurple.domain.comment.dto.ReCommentRequestDto;
import com.onpurple.domain.comment.service.ReCommentService;
import com.onpurple.domain.comment.dto.ReCommentResponseDto;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reComment")
@Tag(name = "대댓글 API", description = "대댓글 생성, 대댓글 조회, 대댓글 수정, 대댓글 삭제")
public class ReCommentController {

    private final ReCommentService reCommentService;


    // 대댓글 작성
    @PostMapping( "/{commentId}")
    @Operation(summary = "대댓글 생성", description = "대댓글 생성")
    @Parameter(name = "commentId", description = "대댓글을 생성할 댓글의 id", required = true)
    @Parameter(name = "reCommentRequestDto", description = "대댓글 생성 정보", required = true)
    @Parameter(name = "userDetails", description = "대댓글을 생성할 사용자의 정보", required = true)
    public ApiResponseDto<ReCommentResponseDto> createReComment(@PathVariable Long commentId,
                                                                @RequestPart(value = "data") final ReCommentRequestDto reCommentRequestDto,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reCommentService.createReComment(commentId, reCommentRequestDto, userDetails.getUser());
    }

    // 대댓글 조회하기
    @GetMapping("/{commentId}")
    @Operation(summary = "댓글의 대댓글 조회", description = "댓글의 대댓글 조회")
    @Parameter(name = "commentId", description = "대댓글을 조회할 댓글의 id", required = true)
    public ApiResponseDto<List<ReCommentResponseDto>> getAllReComment(@PathVariable final Long commentId) {
            return reCommentService.getAllReCommentsByComment(commentId);
        }


    // 대댓글 수정
    @PutMapping( "/{reCommentId}")
    @Operation(summary = "대댓글 수정", description = "대댓글 수정")
    @Parameter(name = "reCommentId", description = "수정할 대댓글의 id", required = true)
    @Parameter(name = "reCommentRequestDto", description = "대댓글 수정 정보", required = true)
    @Parameter(name = "userDetails", description = "대댓글을 수정할 사용자의 정보", required = true)
    public ApiResponseDto<ReCommentResponseDto> updateReComment(@PathVariable final Long reCommentId,
                                                                @RequestPart(value = "data") final ReCommentRequestDto reCommentRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reCommentService.updateReComment(reCommentId, reCommentRequestDto, userDetails.getUser());
    }

    //대댓글삭제
    @DeleteMapping( "/{reCommentId}")
    @Operation(summary = "대댓글 삭제", description = "대댓글 삭제")
    @Parameter(name = "reCommentId", description = "삭제할 대댓글의 id", required = true)
    @Parameter(name = "userDetails", description = "대댓글을 삭제할 사용자의 정보", required = true)
    public ApiResponseDto<MessageResponseDto> deleteReComment(@PathVariable final Long reCommentId,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reCommentService.deleteReComment(reCommentId, userDetails.getUser());
    }
}