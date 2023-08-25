package com.project.date.service;

import com.project.date.dto.request.PostRequestDto;
import com.project.date.dto.request.ReportRequestDto;
import com.project.date.dto.response.CommentResponseDto;
import com.project.date.dto.response.PostResponseDto;
import com.project.date.dto.response.ReportResponseDto;
import com.project.date.dto.response.ResponseDto;
import com.project.date.jwt.TokenProvider;
import com.project.date.model.Img;
import com.project.date.model.Report;
import com.project.date.model.User;
import com.project.date.repository.ReportRepository;
import com.project.date.repository.UserRepository;
import com.project.date.util.AwsS3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final TokenProvider tokenProvider;
    private final AwsS3UploadService awsS3UploadService;

    // 신고글 작성
    @Transactional
    public ResponseDto<?> createReport(ReportRequestDto requestDto,
                                       HttpServletRequest request,
                                       List<String> imgPaths) {

        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("USER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("USER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        User user = validateUser(request);
        if (null == user) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }



        Report report = Report.builder()
                .user(user)
                .reportNickname(requestDto.getReportNickname())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .category(requestDto.getCategory())
                .build();

        reportRepository.save(report);

        postBlankCheck(imgPaths);

        List<String> imgList = new ArrayList<>();
        for (String imgUrl : imgPaths) {
            Img img = new Img(imgUrl, report);
            imgList.add(img.getImageUrl());
        }

        report.imageSave(imgList.get(0));
        return ResponseDto.success(
                ReportResponseDto.builder()
                        .reportId(report.getId())
                        .reportNickname(report.getReportNickname())
                        .title(report.getTitle())
                        .content(report.getContent())
                        .imageUrl(report.getImageUrl())
                        .category(report.getCategory())
                        .createdAt(report.getCreatedAt())
                        .modifiedAt(report.getModifiedAt())
                        .build()
        );
    }

    // 신고글 단건 조회
    @Transactional// readOnly설정시 데이터가 Mapping되지 않는문제로 해제
    public ResponseDto<?> getReport(Long reportId) {
        Report report = isPresentReport(reportId);
        if (null == report) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
        }

        return ResponseDto.success(
                ReportResponseDto.builder()
                        .reportId(report.getId())
                        .title(report.getTitle())
                        .content(report.getContent())
                        .reportNickname(report.getReportNickname())
                        .category(report.getCategory())
                        .imageUrl(report.getImageUrl())
                        .createdAt(report.getCreatedAt())
                        .modifiedAt(report.getModifiedAt())
                        .build()
        );
    }

    // 전체 신고글 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllReport() {
        List<Report> reportList = reportRepository.findAllByOrderByModifiedAtDesc();
        List<ReportResponseDto> reportResponseDto = new ArrayList<>();
        for (Report report : reportList) {
            reportResponseDto.add(
                    ReportResponseDto.builder()
                            .reportId(report.getId())
                            .title(report.getTitle())
                            .imageUrl(report.getImageUrl())
                            .content(report.getContent())
                            .category(report.getCategory())
                            .reportNickname(report.getReportNickname())
                            .createdAt(report.getCreatedAt())
                            .modifiedAt(report.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(reportResponseDto);

    }

    @Transactional
    public ResponseDto<?> deleteReport(Long reportId, HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("USER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("USER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        User user = validateUser(request);
        if (null == user) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Report report = isPresentReport(reportId);
        if (null == report) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
        }

        if (report.validateUser(user)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
        }

        reportRepository.delete(report);
        String deleteImage = report.getImageUrl();
        awsS3UploadService.deleteFile(AwsS3UploadService.getFileNameFromURL(deleteImage));

        return ResponseDto.success("delete success");
    }






    private void postBlankCheck(List<String> imgPaths) {
        if(imgPaths == null || imgPaths.isEmpty()){ //.isEmpty()도 되는지 확인해보기
            throw new NullPointerException("이미지를 등록해주세요(Blank Check)");
        }
    }

    @Transactional
    public User validateUser(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getUserFromAuthentication();
    }

    @Transactional(readOnly = true)
    public Report isPresentReport(Long reportId) {
        Optional<Report> optionalReport = reportRepository.findById(reportId);
        return optionalReport.orElse(null);
    }




}
