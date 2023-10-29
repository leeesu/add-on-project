package com.onpurple.controller;

import com.onpurple.dto.request.ReportRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.ReportResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.ReportService;
import com.onpurple.helper.EntityValidatorManager;
import com.onpurple.external.s3.AwsS3UploadService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReportController {
    private final ReportService reportService;
    private final AwsS3UploadService s3Service;
    private final EntityValidatorManager entityValidatorManager;

    // 신고글 작성
    @PostMapping( "/report")
    public ApiResponseDto<ReportResponseDto> createReport(@RequestPart(value = "data",required = false) final ReportRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @NotNull @RequestPart(value = "imageUrl",required = false) final MultipartFile multipartFiles) {
        String imgPaths = s3Service.uploadOne(multipartFiles);
        return reportService.createReport(requestDto,userDetails.getUser(), imgPaths);
    }

    @GetMapping("/report")
    public ApiResponseDto<List<ReportResponseDto>> getAllPosts() {
        return reportService.getAllReport();
    }

    // 상세 신고글 가져오기
    @GetMapping( "/report/{reportId}")
    public ApiResponseDto<ReportResponseDto> getReport(@PathVariable final Long reportId) {
        return reportService.getReport(reportId);
    }



    //신고글 삭제
    @DeleteMapping( "/report/{reportId}")
    public ApiResponseDto<MessageResponseDto> deleteReport(@PathVariable final Long reportId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.deleteReport(reportId, userDetails.getUser());
    }
}
