package com.onpurple.controller;


import com.onpurple.dto.request.CommentRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.CommentResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.CommentService;
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
  public ApiResponseDto<CommentResponseDto> createComment(@PathVariable Long postId,
                                                          @RequestBody CommentRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.createComment(postId, requestDto, userDetails.getUser());
  }

  @GetMapping("/comment/{postId}")
  public ApiResponseDto<List<CommentResponseDto>> getAllComments(@PathVariable Long postId) {
    return commentService.getAllCommentsByPost(postId);
  }

  @PutMapping( "/comment/{commentId}")
  public ApiResponseDto<CommentResponseDto> updateComment(@PathVariable Long commentId,
                                      @RequestBody CommentRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.updateComment(commentId, requestDto, userDetails.getUser());
  }

  @DeleteMapping( "/comment/{commentId}")
  public ApiResponseDto<MessageResponseDto> deleteComment(@PathVariable Long commentId,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return commentService.deleteComment(commentId, userDetails.getUser());
  }
}
