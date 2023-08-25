package com.onpurple.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
}
