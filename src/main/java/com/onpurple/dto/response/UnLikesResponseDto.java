package com.project.date.dto.response;

import com.project.date.model.Likes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UnLikesResponseDto{
    private Long userId;
    private String imageUrl;
}
