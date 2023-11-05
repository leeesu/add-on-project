package com.onpurple.domain.post.dto;

import com.onpurple.domain.post.category.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
  private String title;
  private String content;
  private List<String> imgList;
  private PostCategory category;
  private String createdAt;
  private String modifiedAt;


}
