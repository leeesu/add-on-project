package com.onpurple.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
  private Long postId;
  private String comment;
  private String createdAt;
  private String modifiedAt;
}
