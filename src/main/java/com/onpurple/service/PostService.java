package com.onpurple.service;


import com.onpurple.dto.request.PostRequestDto;
import com.onpurple.dto.response.CommentResponseDto;
import com.onpurple.dto.response.PostResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.model.Comment;
import com.onpurple.model.Img;
import com.onpurple.model.Post;
import com.onpurple.model.User;
import com.onpurple.repository.CommentRepository;
import com.onpurple.repository.ImgRepository;
import com.onpurple.repository.PostRepository;
import com.onpurple.util.AwsS3UploadService;
import com.onpurple.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ImageUtil imageUtil;


    // 게시글 작성
    @Transactional
    public ResponseDto<PostResponseDto.CreateResponse> createPost(PostRequestDto requestDto,
                                                                  User user, List<String> imgPaths) {
        Post post = postFromRequest(requestDto, user);
        postRepository.save(post);

        List<String> imgList;
        imgList = imageUtil.addImage(imgPaths, post);

        return ResponseDto.success(
                PostResponseDto.CreateResponse.fromEntity(post, imgList));
    }

    private Post postFromRequest(PostRequestDto postRequestDto, User user) {
        return Post.builder()
                .user(user)
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .category(postRequestDto.getCategory())
                .createdAt(formatTime())
                .modifiedAt(formatTime())
                .build();
    }


    // 전체 게시글 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost(String category) {
        List<Post> postList = postRepository.findAllByCategoryOrderByCreatedAtDesc(category);
        List<PostResponseDto> postResponseDto = new ArrayList<>();
        for (Post post : postList) {
            // 이미지 가져오기
            List<String> imgList = imageUtil.getListImage(post);
            postResponseDto.add(
                    PostResponseDto.GetAllResponse.fromEntity(post, imgList)
            );
        }
        return ResponseDto.success(postResponseDto);

    }



    // 게시글 단건 조회
    @Transactional// readOnly설정시 데이터가 Mapping되지 않는문제로 해제
    public ResponseDto<PostResponseDto.DetailResponse> getPost(Long postId) {
        Post post = isPresentPost(postId);
        if (null == post) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        List<CommentResponseDto> commentResponseDtoList = commentRepository.findAllByPost(post).stream()
                .map(CommentResponseDto::fromEntity)
                .collect(Collectors.toList());

        // 단건 조회 조회수 증가
        post.updateViewCount();
        List<String> imgList = imageUtil.getListImage(post);

        return ResponseDto.success(
                PostResponseDto.DetailResponse.fromEntity(
                        post, imgList, commentResponseDtoList)
        );
    }

    //게시글 업데이트
    @Transactional
    public ResponseDto<PostResponseDto.CreateResponse> updatePost(Long postId,
                                                   PostRequestDto requestDto,
                                                   User user,
                                                   List<String> imgPaths) {

        Post post = assertValidatePost(postId);
        validatePostUser(post, user);

        //저장된 이미지 리스트 가져오기
        List<String> newImgList = imageUtil.updateImage(imgPaths, post);

        String modifiedAt = formatTime();

        post.update(requestDto);
        post.updateModified(modifiedAt);
        return ResponseDto.success(
                PostResponseDto.CreateResponse.fromEntity(post, newImgList)
        );
    }
    //게시글 삭제
    @Transactional
    public ResponseDto<?> deletePost(Long postId, User user) {

        Post post = assertValidatePost(postId);
        validatePostUser(post, user);

        postRepository.delete(post);
        List<String> imgList = imageUtil.getListImage(post);
        imageUtil.deleteImageList(post, imgList);
        return ResponseDto.success("delete success");
    }

    //    // 카테고리 조회, 검색
//    @Transactional(readOnly = true)
//    public ResponseDto<?> getAllPostSearch(String keyword) {
//        if((keyword).isEmpty()){
//            return ResponseDto.fail("KEYWORD_NOT_FOUND","검색어가 존재하지 않습니다");
//        }
//
//        List<PostResponseDto> postList = postRepository.findAllByCategorySearch(keyword);
//        if (postList.isEmpty()) {
//            return ResponseDto.fail("POST_NOT_FOUND", "존재하지 않는 게시글입니다.");
//
//        }
//        return ResponseDto.success(postList);
//
//    }

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

    public Post assertValidatePost(Long postId) {
        Post post = isPresentPost(postId);
        if (null == post) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
        return post;
    }




    public void validatePostUser(Post post, User user) {
        if (post.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_USER_MATCH);
        }
    }

}
