package com.onpurple.service;


import com.onpurple.dto.request.ReportRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.ReportResponseDto;
import com.onpurple.category.ReportCategory;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.Report;
import com.onpurple.model.User;
import com.onpurple.repository.ReportRepository;
import com.onpurple.repository.UserRepository;
import com.onpurple.external.s3.AwsS3UploadService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.onpurple.enums.SuccessCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AwsS3UploadService awsS3UploadService;


    /**
     * 신고글 작성
     * @param requestDto
     * @param user
     * @param imgPaths
     * @return ApiResponseDto<ReportResponseDto>
     */
    @Transactional
    public ApiResponseDto<ReportResponseDto> createReport(ReportRequestDto requestDto,
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
        return ApiResponseDto.success(
                SUCCESS_REPORT_REGISTER.getMessage(),
                ReportResponseDto.fromEntity(report)
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

    /**
     * 신고글 단건 조회
     * @param reportId
     * @return ApiResponseDto<ReportResponseDto>
     */
    @Transactional// readOnly설정시 데이터가 Mapping되지 않는문제로 해제
    public ApiResponseDto<ReportResponseDto> getReport(Long reportId) {
        Report report = isPresentReport(reportId);
        if (null == report) {
            throw new CustomException(ErrorCode.REPORT_POST_NOT_FOUND);
        }

        return ApiResponseDto.success(
                SUCCESS_REPORT_GET_DETAIL.getMessage(),
                ReportResponseDto.fromEntity(report)
        );
    }

    /**
     * 신고글 전체 조회
     * @return ApiResponseDto<List<ReportResponseDto>>
     */
    @Transactional(readOnly = true)
    public ApiResponseDto<List<ReportResponseDto>> getAllReport() {
        List<Report> reportList = reportRepository.findAllByOrderByModifiedAtDesc();
        List<ReportResponseDto> reportResponseDto = new ArrayList<>();
        for (Report report : reportList) {
            reportResponseDto.add(
                    ReportResponseDto.AllFromEntity(report)
            );
        }

        return ApiResponseDto.success(
                SUCCESS_REPORT_GET_ALL.getMessage(),
                reportResponseDto);

    }

    /**
     * 신고글 삭제
     * @param reportId
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> deleteReport(Long reportId, User user) {


        Report report = isPresentReport(reportId);
        if (null == report) {
            throw new CustomException(ErrorCode.REPORT_POST_NOT_FOUND);
        }

        if (report.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_USER_MATCH);
        }

        reportRepository.delete(report);
        String deleteImage = report.getImageUrl();
        awsS3UploadService.deleteFile(AwsS3UploadService.getFileNameFromURL(deleteImage));

        return ApiResponseDto.success(SUCCESS_REPORT_DELETE.getMessage());
    }


    /**
     * 신고글 작성자와 로그인한 사용자가 일치하는지 확인
     * @param reportId
     * @return Report
     */
    @Transactional(readOnly = true)
    public Report isPresentReport(@NotNull Long reportId) {
        Optional<Report> optionalReport = reportRepository.findById(reportId);
        return optionalReport.orElse(null);
    }

    /**
     * 카테고리 검증
     * @param category
     */
    public void validateReportCategory(ReportCategory category) {

        if (!ReportCategory.isValidCategory(category)) {
            log.error("[FAIL] {} 카테고리가 존재하지 않습니다.", category);
            throw new CustomException(ErrorCode.POST_CATEGORY_NOT_FOUND);
        }
    }


}
