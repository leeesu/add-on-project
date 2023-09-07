package com.onpurple.controller;

import com.onpurple.dto.request.ReportRequestDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.ReportService;
import com.onpurple.util.ValidationUtil;
import com.onpurple.util.s3.AwsS3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class ReportController {
    private final ReportService reportService;
    private final AwsS3UploadService s3Service;
    private final ValidationUtil validationUtil;

    // 신고글 작성
    @PostMapping( "/report")
    public ResponseDto<?> createReport(@RequestPart(value = "data",required = false) ReportRequestDto requestDto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @RequestPart(value = "imageUrl",required = false) MultipartFile multipartFiles) {

        validationUtil.validateMultipartFile(multipartFiles);
        String imgPaths = s3Service.uploadOne(multipartFiles);
        return reportService.createReport(requestDto,userDetails.getUser(), imgPaths);
    }

    @GetMapping("/report")
    public ResponseDto<?> getAllPosts() {
        return reportService.getAllReport();
    }

    // 상세 신고글 가져오기
    @GetMapping( "/report/{reportId}")
    public ResponseDto<?> getPost(@PathVariable Long reportId) {
        return reportService.getReport(reportId);
    }



    //신고글 삭제
    @DeleteMapping( "/report/{reportId}")
    public ResponseDto<?> deleteReport(@PathVariable Long reportId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.deleteReport(reportId, userDetails.getUser());
    }
}
