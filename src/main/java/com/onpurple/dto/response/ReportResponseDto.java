package com.project.date.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDto {
    private Long reportId;
    private String title;
    private String reportNickname;
    private String content;
    private String imageUrl;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}