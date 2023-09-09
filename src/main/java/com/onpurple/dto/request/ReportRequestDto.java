package com.onpurple.dto.request;

import com.onpurple.enums.ReportCategory;
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
    private ReportCategory category;
    private String reportNickname;



}
