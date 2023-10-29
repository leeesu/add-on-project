package com.onpurple.external;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.onpurple.enums.ResizeEnum;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.external.dto.S3Component;
import com.onpurple.util.ImageResizeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AwsS3UploadService implements UploadService {

    @Value("${cloudFront.url}")
    private String cloudFrontUrl;

    private final AmazonS3 s3Client;
    private final S3Component component;  // AWS S3 를 위한 설정이 담긴 클래스

    public List<String> upload(List<MultipartFile> multipartFile) {
        List<String> imgUrlList = new ArrayList<>();

        for (MultipartFile file : multipartFile) {
            imgUrlList.add(uploadFileReturnUrl(file));
        }
        return imgUrlList;
    }

    public String uploadOne(MultipartFile file) {
        return uploadFileReturnUrl(file);
    }

    private String uploadFileReturnUrl(MultipartFile file) {
        String fileName = createFileName(file.getOriginalFilename());
        String fileFormatName = fileFormatName(file);
        MultipartFile resizedFile = ImageResizeUtil.resizeImage(fileName, fileFormatName, file, ResizeEnum.IMG_WIDTH.getSize());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(resizedFile.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = resizedFile.getInputStream()) {
            // CloudFront 도메인을 사용하여 파일 업로드
            uploadFile(inputStream, objectMetadata, fileName);

            // CloudFront 도메인을 사용하여 이미지 URL 생성
            return getFileUrl(fileName);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("파일 변환 중 에러가 발생하였습니다 (%s)", file.getOriginalFilename()));
        }
    }



    // 파일 업로드 메서드
    @Override
    public void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName) {
        s3Client.putObject(new PutObjectRequest(component.getBucket(), fileName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    // 업로드한 파일의 URL 가져오기
    @Override
    public String getFileUrl(String fileName) {
        return cloudFrontUrl +"/"+ fileName; // CloudFront 도메인으로 수정
    }

    // 파일 삭제
    public void deleteFile(String fileName) {
        try {
            s3Client.deleteObject(component.getBucket(), fileName);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
    }

    public String fileFormatName(MultipartFile file) {
        return file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
    }

    // 파일 이름 생성 (이름 중복 방지 목적)
    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if(fileName.length() == 0) {
            new CustomException(ErrorCode.INVALID_IMAGE_FORMAT);
        }
        ArrayList<String> format = new ArrayList<>(
                Arrays.asList(".jpeg",".jpg",".png",".JPEG",".PNG",".JPG")
        );
        String validate = fileName.substring(fileName.lastIndexOf("."));
        if(!format.contains(validate)) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FORMAT);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    // URL 에서 파일이름(key) 추출
    public static String getFileNameFromURL(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.length());
    }

}