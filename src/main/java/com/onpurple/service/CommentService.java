package com.onpurple.service;


import com.onpurple.dto.request.CommentRequestDto;
import com.onpurple.dto.request.PostRequestDto;
import com.onpurple.dto.response.CommentResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.model.Comment;
import com.onpurple.model.Post;
import com.onpurple.model.User;
import com.onpurple.repository.CommentRepository;
import com.onpurple.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final ValidationUtil validationUtil;

  @Transactional
  public ResponseDto<?> createComment(Long postId, CommentRequestDto commentRequestDto, User user) {
    // post 유효성 검사
    Post post = validationUtil.assertValidatePost(postId);
    Comment comment = commentFromRequest(commentRequestDto, post, user);
    commentRepository.save(comment);
    return ResponseDto.success(
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
  public ResponseDto<?> getAllCommentsByPost(Long postId) {
    Post post = validationUtil.assertValidatePost(postId);

    List<CommentResponseDto> commentResponseDtoList = commentRepository
            .findAllByPost(post)
            .stream()
            .map(CommentResponseDto::fromEntity)
            .collect(Collectors.toList());

    return ResponseDto.success(commentResponseDtoList);
  }

  @Transactional
  public ResponseDto<?> updateComment(Long commentId, CommentRequestDto requestDto, User user) {
      // 이곳에서 validate 메서드에서 예외 발생 가능성이 있는 작업 수행
       // post validate
      validationUtil.assertValidatePost(requestDto.getPostId());
      // comment validate
      Comment comment = validationUtil.assertValidateComment(commentId);
      // user validate
      validateUser(comment, user);

      comment.update(requestDto);

      return ResponseDto.success(CommentResponseDto.fromEntity(comment));
  }


  @Transactional
  public ResponseDto<?> deleteComment(Long commentId, User user) {
    Comment comment = validationUtil.assertValidateComment(commentId);
    validateUser(comment, user);

    commentRepository.delete(comment);
    return ResponseDto.success("success");
  }


  public void validateUser(Comment comment, User user) {
    if (comment.validateUser(user)) {
      throw new CustomException(ErrorCode.INVALID_USER_MATCH);
    }
  }
}
