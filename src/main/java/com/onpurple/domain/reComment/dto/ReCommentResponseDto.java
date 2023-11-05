package com.onpurple.domain.reComment.dto;

import com.onpurple.domain.reComment.model.ReComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReCommentResponseDto {
    private Long reCommentId;
    private String nickname;
    private String reComment;
    private String createdAt;
    private String modifiedAt;


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
