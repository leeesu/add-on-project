package com.onpurple.service;


import com.onpurple.dto.request.ReportRequestDto;
import com.onpurple.dto.response.ReportResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.model.Img;
import com.onpurple.model.Report;
import com.onpurple.model.User;
import com.onpurple.repository.ReportRepository;
import com.onpurple.util.AwsS3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final AwsS3UploadService awsS3UploadService;

    // 신고글 작성
    @Transactional
    public ResponseDto<?> createReport(ReportRequestDto requestDto,
                                       User user,
                                       List<String> imgPaths) {


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






    private void postBlankCheck(List<String> imgPaths) {
        if(imgPaths == null || imgPaths.isEmpty()){ //.isEmpty()도 되는지 확인해보기
            throw new NullPointerException("이미지를 등록해주세요(Blank Check)");
        }
    }


    @Transactional(readOnly = true)
    public Report isPresentReport(Long reportId) {
        Optional<Report> optionalReport = reportRepository.findById(reportId);
        return optionalReport.orElse(null);
    }


}
