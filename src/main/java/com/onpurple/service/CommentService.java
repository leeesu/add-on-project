package com.onpurple.service;


import com.onpurple.dto.request.CommentRequestDto;
import com.onpurple.dto.response.CommentResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.jwt.TokenProvider;
import com.onpurple.model.Comment;
import com.onpurple.model.Post;
import com.onpurple.model.User;
import com.onpurple.repository.CommentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final TokenProvider tokenProvider;
  private final PostService postService;

  @Transactional
  public ResponseDto<?> createComment(Long postId, CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("RefreshToken")) {
      return ResponseDto.fail("USER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("USER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    User user = validateUser(request);
    if (null == user) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = postService.isPresentPost(postId);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
    }
    String createdAt = formatTime();


    Comment comment = Comment.builder()
            .user(user)
            .post(post)
            .comment(requestDto.getComment())
            .createdAt(createdAt)
            .modifiedAt(createdAt)
        .build();
    commentRepository.save(comment);
    return ResponseDto.success(
        CommentResponseDto.builder()
                .commentId(comment.getId())
                .nickname(comment.getUser().getNickname())
                .comment(comment.getComment())
                .likes(comment.getLikes())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build()
    );
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllCommentsByPost(Long postId) {
    Post post = postService.isPresentPost(postId);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      commentResponseDtoList.add(
          CommentResponseDto.builder()
                  .commentId(comment.getId())
                  .nickname(comment.getUser().getNickname())
                  .comment(comment.getComment())
                  .likes(comment.getLikes())
                  .createdAt(comment.getCreatedAt())
                  .modifiedAt(comment.getModifiedAt())
                  .build()
      );
    }
    return ResponseDto.success(commentResponseDtoList);
  }

  @Transactional
  public ResponseDto<?> updateComment(Long commentId, CommentRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("RefreshToken")) {
      return ResponseDto.fail("USER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("USER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    User user = validateUser(request);
    if (null == user) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = postService.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
    }

    Comment comment = isPresentComment(commentId);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글입니다.");
    }

    if (comment.validateUser(user)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    String modifiedAt = formatTime();

    comment.update(requestDto);
    comment.updateModified(modifiedAt);
    return ResponseDto.success(
        CommentResponseDto.builder()
                .commentId(comment.getId())
                .nickname(comment.getUser().getNickname())
                .comment(comment.getComment())
                .likes(comment.getLikes())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build()
    );
  }

  @Transactional
  public ResponseDto<?> deleteComment(Long commentId, HttpServletRequest request) {
    if (null == request.getHeader("RefreshToken")) {
      return ResponseDto.fail("USER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("USER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    User user = validateUser(request);
    if (null == user) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Comment comment = isPresentComment(commentId);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글입니다.");
    }

    if (comment.validateUser(user)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    commentRepository.delete(comment);
    return ResponseDto.success("success");
  }


  //현재시간 추출 메소드
  private String formatTime(){
    Date now = new Date();         // 현재 날짜/시간 출력
    // System.out.println(now); // Thu Jun 17 06:57:32 KST 2021
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return formatter.format(now);

  }


  @Transactional(readOnly = true)
  public Comment isPresentComment(Long commentId) {
    Optional<Comment> optionalComment = commentRepository.findById(commentId);
    return optionalComment.orElse(null);
  }

  @Transactional
  public User validateUser(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
      return null;
    }
    return tokenProvider.getUserFromAuthentication();
  }
}
