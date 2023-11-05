package com.onpurple.domain.report.dto;

import com.onpurple.domain.report.category.ReportCategory;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {

    private String title;
    private String content;
    private String imageUrl;
    private ReportCategory category;
    private String reportNickname;



}
