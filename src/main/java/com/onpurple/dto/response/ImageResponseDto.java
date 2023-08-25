package com.onpurple.dto.response;

import com.onpurple.model.Post;
import com.onpurple.model.User;
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
