package com.project.date.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {

    private String title;
    private String content;
    private String imageUrl;
    private String category;
    private String reportNickname;



}
