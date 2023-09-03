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
  private final PostService postService;

  @Transactional
  public ResponseDto<?> createComment(Long postId, CommentRequestDto commentRequestDto, User user) {
    // post 유효성 검사
    Post post = validatePost(postId);
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
    Post post = validatePost(postId);

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
      validatePost(requestDto.getPostId());
      // comment validate
      Comment comment = validateComment(commentId);
      // user validate
      validateUser(comment, user);

      comment.update(requestDto);

      return ResponseDto.success(CommentResponseDto.fromEntity(comment));
  }


  @Transactional
  public ResponseDto<?> deleteComment(Long commentId, User user) {
    Comment comment = validateComment(commentId);
    validateUser(comment, user);

    commentRepository.delete(comment);
    return ResponseDto.success("success");
  }



  @Transactional(readOnly = true)
  public Comment isPresentComment(Long commentId) {
    Optional<Comment> optionalComment = commentRepository.findById(commentId);
    return optionalComment.orElse(null);
  }

  public Post validatePost(Long postId) {
    Post post = postService.isPresentPost(postId);
    if (post == null) {
      throw new CustomException(ErrorCode.POST_NOT_FOUND);
    }
    return post;
  }

  public Comment validateComment(Long commentId) {
    Comment comment = isPresentComment(commentId);
    if (comment == null) {
      throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
    }
    return comment;
  }

  public void validateUser(Comment comment, User user) {
    if (comment.validateUser(user)) {
      throw new CustomException(ErrorCode.INVALID_USER_MATCH);
    }
  }
}
