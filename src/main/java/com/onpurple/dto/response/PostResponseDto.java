package com.onpurple.dto.response;

import com.onpurple.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    private String category;
    private List<String> imgList;
    private String createdAt;
    private String modifiedAt;


    @SuperBuilder
    @Getter
    @NoArgsConstructor
    public static class DetailResponse extends PostResponseDto { // 상속 키워드 extends 사용
        // 추가적인 필드만 정의
        private List<CommentResponseDto> commentResponseDtoList;

        public static DetailResponse fromEntity(
                Post post, List<String> imgList,
                List<CommentResponseDto> commentResponseDtoList) {
            return DetailResponse.builder()
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
    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    public static class CreateResponse extends PostResponseDto {
        public static CreateResponse fromEntity(
                Post post, List<String> imgList) {
            return CreateResponse.builder()
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
    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    public static class GetAllResponse extends PostResponseDto {
        public static GetAllResponse fromEntity(
                Post post, List<String> imgList) {
            return GetAllResponse.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .imageUrl(imgList.get(0))
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
}

