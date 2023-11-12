package com.onpurple.domain.report.controller;

import com.onpurple.domain.report.dto.ReportRequestDto;

import com.onpurple.domain.report.dto.ReportResponseDto;
import com.onpurple.domain.report.service.ReportService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.external.AwsS3UploadService;
import com.onpurple.global.security.UserDetailsImpl;
import com.onpurple.global.helper.EntityValidatorManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/report")
@Tag(name = "신고글 API", description = "신고글 생성, 전체 신고글 조회, 상세 신고글 조회, 신고글 삭제")
public class ReportController {
    private final ReportService reportService;
    private final AwsS3UploadService s3Service;
    private final EntityValidatorManager entityValidatorManager;

    // 신고글 작성
    @PostMapping
    @Operation(summary = "신고글 생성", description = "신고글 생성")
    @Parameter(name = "reportRequestDto", description = "신고글 생성 정보", required = true)
    @Parameter(name = "userDetails", description = "신고글을 생성할 사용자의 정보", required = true)
    @Parameter(name = "multipartFiles", description = "신고글에 첨부할 이미지 파일(단일)", required = true)
    public ApiResponseDto<ReportResponseDto> createReport(@RequestPart(value = "data",required = false) final ReportRequestDto requestDto,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @NotNull @RequestPart(value = "imageUrl",required = false) final MultipartFile multipartFiles) {
        String imgPaths = s3Service.uploadOne(multipartFiles);
        return reportService.createReport(requestDto,userDetails.getUser(), imgPaths);
    }

    @GetMapping
    @Operation(summary = "전체 신고글 조회", description = "전체 신고글 조회")
    public ApiResponseDto<List<ReportResponseDto>> getAllPosts() {
        return reportService.getAllReport();
    }

    // 상세 신고글 가져오기
    @GetMapping( "/report/{reportId}")
    @Operation(summary = "상세 신고글 조회", description = "상세 신고글 조회")
    @Parameter(name = "reportId", description = "조회할 신고글의 id", required = true)
    public ApiResponseDto<ReportResponseDto> getReport(@PathVariable final Long reportId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.getReport(reportId, userDetails.getUser());
    }



    //신고글 삭제
    @DeleteMapping( "/report/{reportId}")
    public ApiResponseDto<MessageResponseDto> deleteReport(@PathVariable final Long reportId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.deleteReport(reportId, userDetails.getUser());
    }
}
