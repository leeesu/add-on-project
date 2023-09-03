package com.onpurple.controller;

import com.onpurple.dto.request.ReportRequestDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.security.UserDetailsServiceImpl;
import com.onpurple.service.ReportService;
import com.onpurple.util.AwsS3UploadService;
import jakarta.servlet.http.HttpServletRequest;
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

    // 신고글 작성
    @PostMapping( "/report")
    public ResponseDto<?> createReport(@RequestPart(value = "data",required = false) ReportRequestDto requestDto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @RequestPart(value = "imageUrl",required = false) MultipartFile multipartFiles) {

        if (multipartFiles == null) {
            throw new NullPointerException("사진을 업로드해주세요");
        }
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
