package com.project.date.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReCommentResponseDto {
    private Long reCommentId;
    private String nickname;
    private String reComment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
