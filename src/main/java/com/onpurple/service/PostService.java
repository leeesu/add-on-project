package com.project.date.service;


import com.project.date.dto.request.PostRequestDto;
import com.project.date.dto.response.CommentResponseDto;
import com.project.date.dto.response.PostResponseDto;
import com.project.date.dto.response.ResponseDto;
import com.project.date.jwt.TokenProvider;
import com.project.date.model.Comment;
import com.project.date.model.Img;
import com.project.date.model.Post;
import com.project.date.model.User;
import com.project.date.repository.CommentRepository;
import com.project.date.repository.ImgRepository;
import com.project.date.repository.PostRepository;
import com.project.date.util.AwsS3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;
    private final CommentRepository commentRepository;
    private final ImgRepository imgRepository;
    private final AwsS3UploadService awsS3UploadService;


    // 게시글 작성
    @Transactional
    public ResponseDto<?> createPost(PostRequestDto requestDto,
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
        String createdAt = formatTime();

        Post post = Post.builder()
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .category(requestDto.getCategory())
                .createdAt(createdAt)
                .modifiedAt(createdAt)
                .build();

        postRepository.save(post);

        postBlankCheck(imgPaths);

        List<String> imgList = new ArrayList<>();
        for (String imgUrl : imgPaths) {
            Img img = new Img(imgUrl, post);
            imgRepository.save(img);
            imgList.add(img.getImageUrl());
        }

        return ResponseDto.success(
                PostResponseDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .nickname(post.getUser().getNickname())
                        .imgList(imgList)
                        .category(post.getCategory())
                        .likes(post.getLikes())
                        .view(0)
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }

    private void postBlankCheck(List<String> imgPaths) {
        if(imgPaths == null || imgPaths.isEmpty()){ //.isEmpty()도 되는지 확인해보기
            throw new NullPointerException("이미지를 등록해주세요(Blank Check)");
        }
    }


    // 전체 게시글 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost(String category) {
        List<Post> postList = postRepository.findAllByCategoryOrderByCreatedAtDesc(category);
        List<PostResponseDto> postResponseDto = new ArrayList<>();
        for (Post post : postList) {
            List<Img> findImgList = imgRepository.findByPost_Id(post.getId());
            List<String> imgList = new ArrayList<>();
            for (Img img : findImgList) {
                imgList.add(img.getImageUrl());
            }
            postResponseDto.add(
                    PostResponseDto.builder()
                            .postId(post.getId())
                            .title(post.getTitle())
                            .imageUrl(imgList.get(0))
                            .content(post.getContent())
                            .likes(post.getLikes())
                            .view(post.getView())
                            .category(post.getCategory())
                            .nickname(post.getUser().getNickname())
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(postResponseDto);

    }



    // 게시글 단건 조회
    @Transactional// readOnly설정시 데이터가 Mapping되지 않는문제로 해제
    public ResponseDto<?> getPost(Long postId) {
        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
        }
        List<Comment> commentList = commentRepository.findAllByPost(post);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for (Comment comment : commentList) {
            commentResponseDtoList.add(
                    CommentResponseDto.builder()
                            .commentId(comment.getId())
                            .nickname(comment.getUser().getNickname())
                            .comment(comment.getComment())
                            .likes(comment.getLikes())
                            .createdAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }
        //단건 조회 조회수 증가
        post.updateViewCount();

        List<Img> findImgList = imgRepository.findByPost_Id(post.getId());
        List<String> imgList = new ArrayList<>();
        for (Img img : findImgList) {
            imgList.add(img.getImageUrl());
        }

        return ResponseDto.success(
                PostResponseDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .commentResponseDtoList(commentResponseDtoList)
                        .nickname(post.getUser().getNickname())
                        .likes(post.getLikes())
                        .view(post.getView())
                        .category(post.getCategory())
                        .imgList(imgList)
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }
    //게시글 업데이트
    @Transactional
    public ResponseDto<PostResponseDto> updatePost(Long postId,
                                                   PostRequestDto requestDto,
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

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
        }

        if (post.validateUser(user)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }


        //저장된 이미지 리스트 가져오기
        List<Img> findImgList = imgRepository.findByPost_Id(post.getId());
        List<String> imgList = new ArrayList<>();
        for (Img img : findImgList) {
            imgList.add(img.getImageUrl());
        }
        if(imgPaths != null) {
            //s3에 저장되어 있는 img list 삭제
            for (String imgUrl : imgList) {
                awsS3UploadService.deleteFile(AwsS3UploadService.getFileNameFromURL(imgUrl));
            }
            imgRepository.deleteByPost_Id(post.getId());
//            String deleteImage = post.getImageUrl();
//            awsS3UploadService.deleteFile(AwsS3UploadService.getFileNameFromURL(deleteImage));
        }

        List<String> newImgList = new ArrayList<>();
        for (String imgUrl : imgPaths) {
            Img img = new Img(imgUrl, post);
            imgRepository.save(img);
            newImgList.add(img.getImageUrl());
        }

        String modifiedAt = formatTime();

        post.update(requestDto);
        post.updateModified(modifiedAt);
        return ResponseDto.success(
                PostResponseDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .nickname(post.getUser().getNickname())
                        .imgList(newImgList)
                        .view(post.getView())
                        .category(post.getCategory())
                        .likes(post.getLikes())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }
    //게시글 삭제
    @Transactional
    public ResponseDto<?> deletePost(Long postId, HttpServletRequest request) {
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

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
        }

        if (post.validateUser(user)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
        List<Img> findImgList = imgRepository.findByPost_Id(post.getId());
        List<String> imgList = new ArrayList<>();
        for (Img img : findImgList) {
            imgList.add(img.getImageUrl());
        }

        for (String imgUrl : imgList) {
            awsS3UploadService.deleteFile(AwsS3UploadService.getFileNameFromURL(imgUrl));
        }
        return ResponseDto.success("delete success");
    }

    //    // 카테고리 조회, 검색
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPostSearch(String keyword) {
        if((keyword).isEmpty()){
            return ResponseDto.fail("KEYWORD_NOT_FOUND","검색어가 존재하지 않습니다");
        }

        List<PostResponseDto> postList = postRepository.findAllByCategorySearch(keyword);
        if (postList.isEmpty()) {
            return ResponseDto.fail("POST_NOT_FOUND", "존재하지 않는 게시글입니다.");

        }
        return ResponseDto.success(postList);

    }

    private String formatTime(){
        Date now = new Date();         // 현재 날짜/시간 출력
        // System.out.println(now); // Thu Jun 17 06:57:32 KST 2021
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(now);

    }

    @Transactional(readOnly = true)
    public Post isPresentPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
    }

    @Transactional
    public User validateUser(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getUserFromAuthentication();
    }

//    // 카테고리 전체 게시글 조회
//    @Transactional(readOnly = true)
//    public ResponseDto<?> getAllPost(String category, String keyword, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//
//        Slice<PostResponseDto> postList = postRepository.findAllByCategorySearch(category, keyword, pageable);
//        if (postList.isEmpty()) {
//            return ResponseDto.fail("POST_NOT_FOUND", "존재하지 않는 게시글입니다.");
//
//        }
//        return ResponseDto.success(postList);
//
//    }

//    // 카테고리 전체 게시글 조회
//    @Transactional(readOnly = true)
//    public ResponseDto<?> getAllPost(String category, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//
//        Slice<PostResponseDto> postList = postRepository.findAllByCategory(category, pageable);
//        if (postList.isEmpty()) {
//            return ResponseDto.fail("POST_NOT_FOUND", "존재하지 않는 게시글입니다.");
//
//        }
//        return ResponseDto.success(postList);
//
//    }



}
