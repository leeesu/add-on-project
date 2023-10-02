package com.onpurple.dto.request;

import com.onpurple.category.ReportCategory;
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
