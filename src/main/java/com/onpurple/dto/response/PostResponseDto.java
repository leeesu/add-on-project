package com.onpurple.dto.response;

import com.onpurple.category.PostCategory;
import com.onpurple.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    private Long postId;
    private String title;
    private String nickname;
    private String content;
    private String imageUrl;
    private Integer likes;
    private Integer view;
    private PostCategory category;
    private List<String> imgList;
    private List<CommentResponseDto> commentResponseDtoList;
    private String createdAt;
    private String modifiedAt;


    public static PostResponseDto fromEntity(
            Post post, List<String> imgList) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(post.getUser().getNickname())
                .imgList(imgList)
                .category(post.getCategory())
                .likes(post.getLikes())
                .view(0)
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }


    public static PostResponseDto DetailFromEntity(
            Post post, List<String> imgList,
            List<CommentResponseDto> commentResponseDtoList) {
        return PostResponseDto.builder()
                // 부모 클래스의 필드들은 super 키워드로 접근 가능
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .nickname(post.getUser().getNickname())
                .imgList(imgList)
                .category(post.getCategory())
                .likes(post.getLikes())
                .view(0)
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                // 자신의 필드는 그대로 접근 가능
                .commentResponseDtoList(commentResponseDtoList)
                .build();
    }




    public static PostResponseDto GetAllFromEntity(
            Post post) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .imageUrl(post.getImageUrl())
                .content(post.getContent())
                .likes(post.getLikes())
                .view(post.getView())
                .category(post.getCategory())
                .nickname(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }
}


