package com.onpurple.dto.response;

import com.onpurple.category.ReportCategory;
import com.onpurple.model.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private ReportCategory category;
    private String createdAt;
    private String modifiedAt;

    public static ReportResponseDto fromEntity(Report report) {
        return ReportResponseDto.builder()
                .reportId(report.getId())
                .reportNickname(report.getReportNickname())
                .title(report.getTitle())
                .content(report.getContent())
                .imageUrl(report.getImageUrl())
                .category(report.getCategory())
                .createdAt(report.getCreatedAt())
                .modifiedAt(report.getModifiedAt())
                .build();
    }

    public static ReportResponseDto AllFromEntity(Report report) {
        return ReportResponseDto.builder()
                .reportId(report.getId())
                .reportNickname(report.getReportNickname())
                .title(report.getTitle())
                .imageUrl(report.getImageUrl())
                .category(report.getCategory())
                .createdAt(report.getCreatedAt())
                .modifiedAt(report.getModifiedAt())
                .build();
    }


}
