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
            .createdAt(formatTime())
            .modifiedAt(formatTime())
            .build();
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
          CommentResponseDto.fromEntity(comment)
      );
    }
    return ResponseDto.success(commentResponseDtoList);
  }

  @Transactional
  public ResponseDto<?> updateComment(Long commentId, CommentRequestDto requestDto, User user) {

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
        CommentResponseDto.fromEntity(comment)
    );
  }

  @Transactional
  public ResponseDto<?> deleteComment(Long commentId, User user) {

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

  public void validateAuthor(Comment comment, User user) {
    if (comment.validateUser(user)) {
      throw new CustomException(ErrorCode.INVALID_USER_MATCH);
    }
  }
}
