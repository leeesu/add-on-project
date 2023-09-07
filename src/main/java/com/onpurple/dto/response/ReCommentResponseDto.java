package com.onpurple.dto.response;

import com.onpurple.model.ReComment;
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


    public static ReCommentResponseDto fromEntity(ReComment reComment) {
        return ReCommentResponseDto.builder()
                .reCommentId(reComment.getId())
                .nickname(reComment.getUser().getNickname())
                .reComment(reComment.getReComment())
                .createdAt(reComment.getCreatedAt())
                .modifiedAt(reComment.getModifiedAt())
                .build();

    }
}
