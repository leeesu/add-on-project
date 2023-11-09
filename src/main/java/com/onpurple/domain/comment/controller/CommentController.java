package com.onpurple.domain.comment.controller;


import com.onpurple.domain.comment.dto.CommentRequestDto;

import com.onpurple.domain.comment.dto.CommentResponseDto;
import com.onpurple.domain.comment.service.CommentService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
@Tag(name = "댓글 API", description = "댓글 생성, 댓글 조회, 댓글 수정, 댓글 삭제")
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/{postId}")
  @Operation(summary = "댓글 생성", description = "댓글 생성")
  @Parameter(name = "postId", description = "댓글을 생성할 게시글의 id", required = true)
  @Parameter(name = "commentRequestDto", description = "댓글 생성 정보", required = true)
  public ApiResponseDto<CommentResponseDto> createComment(@PathVariable final Long postId,
                                                          @RequestBody final CommentRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.createComment(postId, requestDto, userDetails.getUser());
  }

  @GetMapping("/{postId}")
  @Operation(summary = "게시글의 댓글 조회", description = "게시글의 댓글 조회")
  @Parameter(name = "postId", description = "댓글을 조회할 게시글의 id", required = true)
  public ApiResponseDto<List<CommentResponseDto>> getAllComments(@PathVariable final Long postId) {
    return commentService.getAllCommentsByPost(postId);
  }

  @PutMapping( "/{commentId}")
  @Operation(summary = "댓글 수정", description = "댓글 수정")
  @Parameter(name = "commentId", description = "수정할 댓글의 id", required = true)
  @Parameter(name = "commentRequestDto", description = "댓글 수정 정보", required = true)
  @Parameter(name = "userDetails", description = "댓글을 수정할 사용자의 정보", required = true)
  public ApiResponseDto<CommentResponseDto> updateComment(@PathVariable final Long commentId,
                                      @RequestBody final CommentRequestDto commentRequestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.updateComment(commentId, commentRequestDto, userDetails.getUser());
  }

  @DeleteMapping( "/{commentId}")
  @Operation(summary = "댓글 삭제", description = "댓글 삭제")
  @Parameter(name = "commentId", description = "삭제할 댓글의 id", required = true)
  @Parameter(name = "userDetails", description = "댓글을 삭제할 사용자의 정보", required = true)
  public ApiResponseDto<MessageResponseDto> deleteComment(@PathVariable final Long commentId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.deleteComment(commentId, userDetails.getUser());
  }
}
