package com.onpurple.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.onpurple.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
  private Long commentId;
  private String nickname;
  private String comment;
  private Integer likes;
  private List<ReCommentResponseDto> reCommentResponseDtoList;
  private String createdAt;
  private String modifiedAt;

  public static CommentResponseDto fromEntity(Comment comment) {
    return CommentResponseDto.builder()
            .commentId(comment.getId())
            .nickname(comment.getUser().getNickname())
            .comment(comment.getComment())
            .likes(comment.getLikes())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build();
  }
}
