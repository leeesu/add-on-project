package com.onpurple.controller;


import com.onpurple.dto.request.CommentRequestDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/comment/{postId}")
  public ResponseDto<?> createComment(@PathVariable Long postId, @RequestBody CommentRequestDto requestDto,
                                      HttpServletRequest request) {
    return commentService.createComment(postId, requestDto, request);
  }

  @GetMapping("/comment/{postId}")
  public ResponseDto<?> getAllComments(@PathVariable Long postId) {
    return commentService.getAllCommentsByPost(postId);
  }

  @PutMapping( "/comment/{commentId}")
  public ResponseDto<?> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto requestDto,
      HttpServletRequest request) {
    return commentService.updateComment(commentId, requestDto, request);
  }

  @DeleteMapping( "/comment/{commentId}")
  public ResponseDto<?> deleteComment(@PathVariable Long commentId,
      HttpServletRequest request) {
    return commentService.deleteComment(commentId, request);
  }
}
