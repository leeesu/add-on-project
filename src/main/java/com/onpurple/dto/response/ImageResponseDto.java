package com.project.date.dto.response;


import com.project.date.model.Post;
import com.project.date.model.User;
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
