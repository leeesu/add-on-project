package com.project.date.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.date.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

import java.util.List;

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


}
