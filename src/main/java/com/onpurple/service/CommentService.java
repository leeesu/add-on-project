package com.onpurple.service;


import com.onpurple.dto.request.CommentRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.CommentResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.Comment;
import com.onpurple.model.Post;
import com.onpurple.model.User;
import com.onpurple.repository.CommentRepository;
import com.onpurple.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.onpurple.enums.SuccessCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final ValidationUtil validationUtil;

  @Transactional
  public ApiResponseDto<CommentResponseDto> createComment(Long postId, CommentRequestDto commentRequestDto, User user) {
    // post 유효성 검사
    Post post = validationUtil.validatePost(postId);
    Comment comment = commentFromRequest(commentRequestDto, post, user);
    commentRepository.save(comment);
    return ApiResponseDto.success(COMMENT_REGISTER.getMessage(),
        CommentResponseDto.fromEntity(comment)
    );
  }

  private Comment commentFromRequest(CommentRequestDto commentRequestDto,
                                     Post post, User user) {
    return Comment.builder()
            .user(user)
            .post(post)
            .comment(commentRequestDto.getComment())
            .build();
  }

  @Transactional(readOnly = true)
  public ApiResponseDto<List<CommentResponseDto>> getAllCommentsByPost(Long postId) {
    Post post = validationUtil.validatePost(postId);

    List<CommentResponseDto> commentResponseDtoList = commentRepository
            .findAllByPost(post)
            .stream()
            .map(CommentResponseDto::fromEntity)
            .collect(Collectors.toList());

    return ApiResponseDto.success(
            COMMENT_GET_ALL.getMessage(),
            commentResponseDtoList);
  }

  @Transactional
  public ApiResponseDto<CommentResponseDto> updateComment(Long commentId, CommentRequestDto requestDto, User user) {
      // 이곳에서 validate 메서드에서 예외 발생 가능성이 있는 작업 수행
       // 게시글 유효성체크
      validationUtil.validatePost(requestDto.getPostId());
      // comment validate
      Comment comment = validationUtil.validateComment(commentId);
      // user validate
      validateUser(comment, user);

      comment.update(requestDto);

      return ApiResponseDto.success(COMMENT_EDIT.getMessage(),CommentResponseDto.fromEntity(comment));
  }


  @Transactional
  public ApiResponseDto<MessageResponseDto> deleteComment(Long commentId, User user) {
    Comment comment = validationUtil.validateComment(commentId);
    validateUser(comment, user);

    commentRepository.delete(comment);
    return ApiResponseDto.success(COMMENT_DELETE.getMessage());
  }


  public void validateUser(Comment comment, User user) {
    if (comment.validateUser(user)) {
      throw new CustomException(ErrorCode.INVALID_USER_MATCH);
    }
  }
}
