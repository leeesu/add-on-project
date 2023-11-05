package com.onpurple.domain.comment.controller;


import com.onpurple.domain.comment.dto.CommentRequestDto;

import com.onpurple.domain.comment.dto.CommentResponseDto;
import com.onpurple.domain.comment.service.CommentService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/comment/{postId}")
  public ApiResponseDto<CommentResponseDto> createComment(@PathVariable final Long postId,
                                                          @RequestBody final CommentRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.createComment(postId, requestDto, userDetails.getUser());
  }

  @GetMapping("/comment/{postId}")
  public ApiResponseDto<List<CommentResponseDto>> getAllComments(@PathVariable final Long postId) {
    return commentService.getAllCommentsByPost(postId);
  }

  @PutMapping( "/comment/{commentId}")
  public ApiResponseDto<CommentResponseDto> updateComment(@PathVariable final Long commentId,
                                      @RequestBody final CommentRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.updateComment(commentId, requestDto, userDetails.getUser());
  }

  @DeleteMapping( "/comment/{commentId}")
  public ApiResponseDto<MessageResponseDto> deleteComment(@PathVariable final Long commentId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.deleteComment(commentId, userDetails.getUser());
  }
}
