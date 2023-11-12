package com.onpurple.domain.report.service;


import com.onpurple.domain.report.dto.ReportRequestDto;
import com.onpurple.domain.report.dto.ReportResponseDto;
import com.onpurple.domain.report.category.ReportCategory;
import com.onpurple.domain.report.model.Report;
import com.onpurple.domain.report.repository.ReportRepository;
import com.onpurple.domain.user.model.User;
import com.onpurple.domain.user.repository.UserRepository;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.exception.CustomException;
import com.onpurple.global.enums.ErrorCode;
import com.onpurple.global.external.AwsS3UploadService;
import com.onpurple.global.helper.EntityValidatorManager;
import com.onpurple.global.role.Authority;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.onpurple.global.enums.SuccessCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AwsS3UploadService awsS3UploadService;
    private final EntityValidatorManager entityValidatorManager;


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
        target.increaseReportCount();

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
     * 신고글 단건 조회, ADMIN과 글쓴이만 조회 가능한 게시판
     * @param reportId
     * @return ApiResponseDto<ReportResponseDto>
     */
    @Transactional// readOnly설정시 데이터가 Mapping되지 않는문제로 해제
    public ApiResponseDto<ReportResponseDto> getReport(Long reportId, User user) {

        Authority role = user.getRole();
        Report report = isPresentReport(reportId);

        // 회원등급이 ADMIN이 아니고 글쓴이가 아닐 경우 조회 불가
        if(role.equals(Authority.USER) && !report.validateUser(user)) {
            throw new CustomException(ErrorCode.NOT_ADMIN_ERROR);
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
        List<ReportResponseDto> reportResponseDtoList = reportRepository.findAllByOrderByModifiedAtDesc()
                .stream()
                .map(ReportResponseDto::AllFromEntity)
                .collect(Collectors.toList());

        return ApiResponseDto.success(
                SUCCESS_REPORT_GET_ALL.getMessage(),
                reportResponseDtoList);
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
        return optionalReport.orElseThrow(
                () -> new CustomException(ErrorCode.REPORT_POST_NOT_FOUND));
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
