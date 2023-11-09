package com.onpurple.global.img.dto;

import com.onpurple.domain.post.model.Post;
import com.onpurple.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDto {

    private Long id;
    private String imageUrl;
    private Post post;
    private User user;
}
