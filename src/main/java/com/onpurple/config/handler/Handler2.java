package com.onpurple.config.handler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.onpurple.enums.ResizeEnum.*;

public class Handler2 implements RequestHandler<S3Event, String> {
    private static final Logger logger = LoggerFactory.getLogger(Handler2.class);
    private final String REGEX = ".*\\.([^\\.]*)";
    private final String JPG_TYPE = "jpg";
    private final String JPG_MIME = "image/jpeg";
    private final String PNG_TYPE = "png";
    private final String PNG_MIME = "image/png";

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            S3EventNotificationRecord record = s3event.getRecords().get(0);

            String srcBucket = record.getS3().getBucket().getName();

            // Object key may have spaces or unicode non-ASCII characters.
            String srcKey = record.getS3().getObject().getUrlDecodedKey();

            String dstBucket = srcBucket;
            String dstKey = "resized-" + srcKey;

            // 이미지 유형 추론
            Matcher matcher = Pattern.compile(REGEX).matcher(srcKey);
            if (!matcher.matches()) {
                logger.info(srcKey + "의 이미지 유형이 올바르지 않습니다.");
                throw new CustomException(ErrorCode.INVALID_IMAGE_FORMAT);
            }
            String imageType = matcher.group(1);
            if (!(JPG_TYPE.equals(imageType)) && !(PNG_TYPE.equals(imageType))) {
                logger.info(srcKey + "의 확장자가 올바르지 않습니다.");
                throw new CustomException(ErrorCode.INVALID_IMAGE_TYPE);
            }

            // S3에서 이미지 다운로드
            S3Client s3Client = S3Client.builder().build();
            InputStream s3Object = getObject(s3Client, srcBucket, srcKey);

            // 원본 이미지 읽고 크기 조정
            BufferedImage srcImage = ImageIO.read(s3Object);
            BufferedImage newImage = resizeImage(srcImage);

            // 대상 형식으로 이미지 다시 인코딩
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(newImage, imageType, outputStream);

            // 새 이미지를 S3에 업로드
            putObject(s3Client, outputStream, dstBucket, dstKey, imageType);

            logger.info("성공적으로 " + srcBucket + "/"
                    + srcKey + "를 크기 조정하고 " + dstBucket + "/" + dstKey + "에 업로드했습니다.");
            return "Ok";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getObject(S3Client s3Client, String bucket, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest);
    }

    private void putObject(S3Client s3Client, ByteArrayOutputStream outputStream,
                           String bucket, String key, String imageType) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Length", Integer.toString(outputStream.size()));
        if (JPG_TYPE.equals(imageType)) {
            metadata.put("Content-Type", JPG_MIME);
        } else if (PNG_TYPE.equals(imageType)) {
            metadata.put("Content-Type", PNG_MIME);
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .metadata(metadata)
                .build();

        // S3 대상 버킷에 업로드
        logger.info(bucket + "/" + key + "에 쓰는 중");
        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(outputStream.toByteArray()));
        }
        catch(AwsServiceException e)
        {
            logger.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * 이미지를 작은 썸네일 크기로 조정합니다.
     *
     * 새 이미지는 원본 이미지를 기반으로 비례적으로 축소된다.
     * 축소 비율은 IMAGE_WIDTH, IMAGE_HEIGHT의 값에 따라 결정된다.
     * 새 이미지는 max(높이, 너비) = IMAGE_HEIGHT * IMAGE_WIDTH의 크기가 된다.
     *
     * @param srcImage 크기를 조정할 BufferedImage입니다.
     * @return 썸네일 크기로 축소된 새 BufferedImage입니다.
     */
    private BufferedImage resizeImage(BufferedImage srcImage) {
        int srcHeight = srcImage.getHeight();
        int srcWidth = srcImage.getWidth();
        // 이미지를 비례적으로 늘리지 않도록 축척 비율을 추론합니다.
        float scalingFactor = Math.min(
                IMG_WIDTH.getSize() / srcWidth, IMG_HEIGHT.getSize() / srcHeight);
        int width = (int) (scalingFactor * srcWidth);
        int height = (int) (scalingFactor * srcHeight);

        BufferedImage resizedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        // 반투명 (알파) 이미지를 적용하기 전에 흰색으로 채웁니다.
        graphics.setPaint(Color.white);
        graphics.fillRect(0, 0, width, height);
        // 간단한 양선형 크기 조정
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(srcImage, 0, 0, width, height, null);
        graphics.dispose();
        return resizedImage;
    }
}
