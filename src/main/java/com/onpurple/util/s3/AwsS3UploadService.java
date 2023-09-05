package com.onpurple.util.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.util.s3.dto.S3Component;
import lombok.RequiredArgsConstructor;
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
public class
AwsS3UploadService implements UploadService {

    private final AmazonS3 s3Client;
    private final S3Component component;  // AWS S3 를 위한 설정이 담긴 클래스

    public List<String> upload(List<MultipartFile> multipartFile) {
        List<String> imgUrlList = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(new PutObjectRequest(component.getBucket(), fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(s3Client.getUrl(component.getBucket(), fileName).toString());
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("파일 변환 중 에러가 발생하였습니다 (%s)", file.getOriginalFilename()));
            }
        }
        return imgUrlList;
    }

    // 단일 이미지 업로드
    public String uploadOne(MultipartFile file) {
        String imageName="";

        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(new PutObjectRequest(component.getBucket(), fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            imageName = s3Client.getUrl(component.getBucket(), fileName).toString();
        } catch (IOException e) {
            new CustomException(ErrorCode.IMAGE_CONVERT_FAILD);
        }
        return imageName;
    }





    // Amazon S3 를 사용해서 파일 업로드
    @Override
    public void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName) {
        s3Client.putObject(new PutObjectRequest(component.getBucket(), fileName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    // 업로드한 파일의 Url 가져오는
    @Override
    public String getFileUrl(String fileName) {
        return s3Client.getUrl(component.getBucket(), fileName).toString();
    }

    // 파일 삭제
    public void deleteFile(String fileName) {
        try {
            s3Client.deleteObject(component.getBucket(), (fileName).replace(File.separatorChar, '/'));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
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