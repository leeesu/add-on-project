package com.onpurple.service;


import com.onpurple.dto.request.PostRequestDto;
import com.onpurple.dto.response.*;
import com.onpurple.category.PostCategory;
import com.onpurple.enums.SuccessCode;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.Post;
import com.onpurple.model.User;
import com.onpurple.repository.CommentRepository;
import com.onpurple.repository.PostRepository;
import com.onpurple.util.ImageUtil;
import com.onpurple.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.onpurple.enums.SuccessCode.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "커뮤니티 기능")
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ImageUtil imageUtil;
    private final ValidationUtil validationUtil;



    // 게시글 작성
    @Transactional
    public ApiResponseDto<PostResponseDto> createPost(PostRequestDto requestDto,
                                                      User user, List<String> imgPaths) {
        // 카테고리 Business Validation
        validateCategory(requestDto.getCategory());
        Post post = postFromRequest(requestDto, user);
        postRepository.save(post);

        List<String> imgList;
        imgList = imageUtil.addImage(imgPaths, post);
        post.saveImage(imgList.get(0));

        return ApiResponseDto.success(
                SUCCESS_POST_REGISTER.getMessage(),
                PostResponseDto.fromEntity(post, imgList));
    }

    private Post postFromRequest(PostRequestDto postRequestDto, User user) {
        return Post.builder()
                .user(user)
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .category(postRequestDto.getCategory())
                .build();
    }


    // 카테고리 전체 게시글 조회
    @Transactional(readOnly = true)
    public ApiResponseDto<Slice<PostResponseDto>> getAllPostCategory(PostCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<PostResponseDto> postList = postRepository.findAllByCategory(category, pageable);
        if (postList.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);

        }
        return ApiResponseDto.success(
                SUCCESS_POST_GET_ALL_CATEGORY.getMessage(),
                postList);

    }


    // 카테고리 검색
    @Transactional(readOnly = true)
    public ApiResponseDto<Slice<PostResponseDto>> getAllPostCategorySearch(PostCategory category, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Slice<PostResponseDto> postList = postRepository.findAllByCategorySearchScroll(category, keyword, pageable);
        if (postList.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);

        }
        return ApiResponseDto.success(
                SUCCESS_POST_GET_ALL_CATEGORY_SEARCH.getMessage(),
                postList);

    }



    // 게시글 단건 조회
    @Transactional// readOnly설정시 데이터가 Mapping되지 않는문제로 해제
    public ApiResponseDto<PostResponseDto> getPost(Long postId) {
        Post post = validationUtil.validatePost(postId);

        List<CommentResponseDto> commentResponseDtoList = commentRepository.findAllByPost(post).stream()
                .map(CommentResponseDto::fromEntity)
                .collect(Collectors.toList());

        // 단건 조회 조회수 증가
        post.updateViewCount();
        List<String> imgList = imageUtil.getListImage(post);

        return ApiResponseDto.success(
                SUCCESS_POST_GET_DETAIL.getMessage(),
                PostResponseDto.DetailFromEntity(
                        post, imgList, commentResponseDtoList)
        );
    }

    //게시글 업데이트
    @Transactional
    public ApiResponseDto<PostResponseDto> updatePost(Long postId,
                                                   PostRequestDto requestDto,
                                                   User user,
                                                   List<String> imgPaths) {

        Post post = validationUtil.validatePost(postId);
        validatePostUser(post, user);

        //저장된 이미지 리스트 가져오기
        List<String> newImgList = imageUtil.updateImage(imgPaths, post);


        post.update(requestDto);
        return ApiResponseDto.success(
                SUCCESS_POST_EDIT.getMessage(),
                PostResponseDto.fromEntity(post, newImgList)
        );
    }
    //게시글 삭제
    @Transactional
    public ApiResponseDto<MessageResponseDto> deletePost(Long postId, User user) {

        Post post = validationUtil.validatePost(postId);
        validatePostUser(post, user);

        postRepository.delete(post);
        List<String> imgList = imageUtil.getListImage(post);
        imageUtil.deleteImageList(post, imgList);
        return ApiResponseDto.success(SUCCESS_POST_DELETE.getMessage());
    }


    public void validatePostUser(Post post, User user) {
        if (post.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_USER_MATCH);
        }
    }

    public void validateCategory(PostCategory category) {

        if (!PostCategory.isValidCategory(category)) {
            log.error("[FAIL] {} 카테고리가 존재하지 않습니다.", category);
            throw new CustomException(ErrorCode.POST_CATEGORY_NOT_FOUND);
        }
    }



}
