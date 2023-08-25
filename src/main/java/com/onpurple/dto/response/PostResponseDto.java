package com.project.date.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String title;
    private String nickname;
    private String content;
    private String imageUrl;
    private Integer likes;
    private Integer view;
    private String category;
    private List<String> imgList;
    private List<CommentResponseDto> commentResponseDtoList;
    private String createdAt;
    private String modifiedAt;

}


