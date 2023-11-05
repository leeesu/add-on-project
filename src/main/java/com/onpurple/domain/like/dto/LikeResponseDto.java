package com.onpurple.domain.like.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.onpurple.domain.like.model.Likes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LikeResponseDto {

    private Long likeId;
    private String imageUrl;
    private Integer likes;
    private String nickname;

    public static LikeResponseDto fromPostLikesEntity(Likes postLikes) {
        return LikeResponseDto.builder()
                .likes(postLikes.getPost().getLikes())
                .build();
    }
    public static LikeResponseDto fromCommentLikesEntity(Likes commentLikes){
        return LikeResponseDto.builder()
                .likes(commentLikes.getComment().getLikes())
                .build();
    }




}
