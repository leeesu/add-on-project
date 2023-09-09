package com.onpurple.service;


import com.onpurple.dto.request.PostRequestDto;
import com.onpurple.dto.request.ReportRequestDto;
import com.onpurple.dto.response.ReportResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.enums.PostCategory;
import com.onpurple.enums.ReportCategory;
import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.model.Post;
import com.onpurple.model.Report;
import com.onpurple.model.User;
import com.onpurple.repository.ReportRepository;
import com.onpurple.repository.UserRepository;
import com.onpurple.util.s3.AwsS3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AwsS3UploadService awsS3UploadService;

    // 신고글 작성
    @Transactional
    public ResponseDto<?> createReport(ReportRequestDto requestDto,
                                       User user,
                                       String imgPaths) {
        //신고하는 회원이 존재하는지 회원인지 확인
        User target = userRepository.findByNickname(requestDto.getReportNickname()).orElseThrow(
                ()-> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        // 본인은 신고할 수 없도록 처리
        if(user == target) {
            throw new CustomException(ErrorCode.INVALID_SELF_REPORT);
        }
        // 카테고리 Business validation
        validateReportCategory(requestDto.getCategory());
        Report report = ReportFromRequest(requestDto, user, imgPaths);

        reportRepository.save(report);
        return ResponseDto.success(ReportResponseDto.fromEntity(report)
        );
    }

    private Report ReportFromRequest(ReportRequestDto reportRequestDto, User user, String imgPaths) {
        Report report = Report.builder()
                .user(user)
                .reportNickname(reportRequestDto.getReportNickname())
                .title(reportRequestDto.getTitle())
                .content(reportRequestDto.getContent())
                .imageUrl(imgPaths)
                .category(reportRequestDto.getCategory())
                .build();
        return report;
    }

    // 신고글 단건 조회
    @Transactional// readOnly설정시 데이터가 Mapping되지 않는문제로 해제
    public ResponseDto<?> getReport(Long reportId) {
        Report report = isPresentReport(reportId);
        if (null == report) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
        }

        return ResponseDto.success(
                ReportResponseDto.fromEntity(report)
        );
    }

    // 전체 신고글 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllReport() {
        List<Report> reportList = reportRepository.findAllByOrderByModifiedAtDesc();
        List<ReportResponseDto> reportResponseDto = new ArrayList<>();
        for (Report report : reportList) {
            reportResponseDto.add(
                    ReportResponseDto.AllFromEntity(report)
            );
        }

        return ResponseDto.success(reportResponseDto);

    }

    @Transactional
    public ResponseDto<?> deleteReport(Long reportId, User user) {


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


    @Transactional(readOnly = true)
    public Report isPresentReport(Long reportId) {
        Optional<Report> optionalReport = reportRepository.findById(reportId);
        return optionalReport.orElse(null);
    }

    public void validateReportCategory(ReportCategory category) {

        if (!ReportCategory.isValidCategory(category)) {
            log.error("[FAIL] {} 카테고리가 존재하지 않습니다.", category);
            throw new CustomException(ErrorCode.POST_CATEGORY_NOT_FOUND);
        }
    }


}
