package com.onpurple.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import marvin.image.MarvinImage;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageResizeUtil {

    public static MultipartFile resizeImage(String fileName, String fileFormatName, MultipartFile originalImage, int targetWidth) {
        try {
            // MultipartFile로 이미지를 받아와서 BufferedImage로 변환
            BufferedImage image = ImageIO.read(originalImage.getInputStream());
            // newWidth : newHeight = originWidth : originHeight
            int originalWidth = image.getWidth(); //
            int originalHeight = image.getHeight();

            // origin 이미지가 resizing될 사이즈보다 작을 경우 resizing 작업 안 함
            if(originalWidth < targetWidth)
                return originalImage;

            MarvinImage imageMarvin = new MarvinImage(image);

            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", targetWidth);
            scale.setAttribute("newHeight", targetWidth * originalHeight / originalWidth);
            scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

            BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(imageNoAlpha, fileFormatName, byteArrayOutputStream);
            byteArrayOutputStream.flush();

            return new MockMultipartFile(fileName, byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 리사이즈에 실패했습니다.");
        }
    }

}
