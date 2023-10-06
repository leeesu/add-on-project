package com.onpurple.external;

import com.onpurple.model.Img;
import com.onpurple.model.Post;
import com.onpurple.repository.ImgRepository;
import com.onpurple.external.s3.AwsS3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ImageUtil {

    private final ImgRepository imgRepository;
    private final AwsS3UploadService awsS3UploadService;

    // 이미지 저장
    public List<String> addImage(List<String> imgPaths, Post post) {

        List<String> imgList = imgPaths.stream()
                .map(imgUrl -> {
                    Img img = new Img(imgUrl, post);
                    imgRepository.save(img);
                    return img.getImageUrl();
                })
                .collect(Collectors.toList());

        return imgList;
    }

    // 이미지 리스트 업데이트
    public List<String> updateImage(List<String> imgPaths, Post post) {
        //저장된 이미지 리스트 가져오기
        List<String> imgList = getListImage(post);
        if (imgPaths != null) {
            // 이미지 삭제
            deleteImageList(post, imgList);
        }
        imgList = addImage(imgPaths, post);
        post.saveImage(imgList.get(0));
        return imgList;
    }
    //이미지 리스트 가져오기
    public List<String> getListImage(Post post) {
        List<String> imgList = imgRepository.findByPost_Id(post.getId()).stream()
                .map(Img::getImageUrl)
                .collect(Collectors.toList());
        return imgList;
    }
    // 이미지 삭제
    public void deleteImageList(Post post, List<String> imgList) {
            // aws s3에서 이미지 삭제
            imgList.stream()
                    .map(AwsS3UploadService::getFileNameFromURL)
                    .forEach(awsS3UploadService::deleteFile);
            // imgRepository에서 post에 해당하는 이미지 삭제
            imgRepository.deleteByPost_Id(post.getId());
    }

}
