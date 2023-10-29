package com.onpurple.service;


import com.onpurple.dto.request.PostRequestDto;
import com.onpurple.dto.response.*;
import com.onpurple.category.PostCategory;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.Post;
import com.onpurple.model.User;
import com.onpurple.repository.CommentRepository;
import com.onpurple.repository.PostRepository;
import com.onpurple.helper.ImageUploaderManager;
import com.onpurple.helper.EntityValidatorManager;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.onpurple.enums.SuccessCode.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "커뮤니티 기능")
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ImageUploaderManager imageUploaderManager;
    private final EntityValidatorManager entityValidatorManager;

    /**
     * 게시글 작성
     * @param postRequestDto
     * @param user
     * @param imgPaths
     * @return ApiResponseDto<PostResponseDto>
     */
    @Transactional
    public ApiResponseDto<PostResponseDto> createPost(PostRequestDto postRequestDto,
                                                      User user, List<String> imgPaths) {
        // 카테고리 Business Validation
        validateCategory(postRequestDto.getCategory());
        Post post = postFromRequest(postRequestDto, user);
        postRepository.save(post);

        List<String> imgList = imageUploaderManager.addImage(imgPaths, post);
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


    /**
     * 카테고리 전체 게시글 조회 (무한스크롤)
     * @param category
     * @param pageable
     * @return ApiResponseDto<Slice<PostResponseDto>>
     */
    @Transactional(readOnly = true)
    public ApiResponseDto<Slice<PostResponseDto>> getAllPostCategory(PostCategory category, Pageable pageable){

        Slice<PostResponseDto> postList = postRepository.findAllByCategory(category, pageable);
        if (postList.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);

        }
        return ApiResponseDto.success(
                SUCCESS_POST_GET_ALL_CATEGORY.getMessage(),
                postList);

    }

    /**
     * 카테고리 검색 (무한스크롤)
     * @param category
     * @param keyword
     * @param pageable
     * @return ApiResponseDto<Slice<PostResponseDto>>
     */
    @Transactional(readOnly = true)
    public ApiResponseDto<Slice<PostResponseDto>> getAllPostCategorySearch(
            PostCategory category, String keyword, Pageable pageable) {

        Slice<PostResponseDto> postList =
                postRepository.findAllByCategorySearchScroll(category, keyword, pageable);
        if (postList.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);

        }
        return ApiResponseDto.success(
                SUCCESS_POST_GET_ALL_CATEGORY_SEARCH.getMessage(),
                postList);

    }

    /**
     * 게시글 단건 조회
     * @param postId
     * @return ApiResponseDto<PostResponseDto>
     */
    @Transactional// readOnly설정시 데이터가 Mapping되지 않는문제로 해제
    public ApiResponseDto<PostResponseDto> getPost(Long postId) {
        Post post = entityValidatorManager.validatePost(postId);

        List<CommentResponseDto> commentResponseDtoList = commentRepository.findAllByPost(post).stream()
                .map(CommentResponseDto::fromEntity)
                .collect(Collectors.toList());

        // 단건 조회 조회수 증가
        viewCount(post);
        List<String> imgList = imageUploaderManager.getListImage(post);

        return ApiResponseDto.success(
                SUCCESS_POST_GET_DETAIL.getMessage(),
                PostResponseDto.DetailFromEntity(
                        post, imgList, commentResponseDtoList)
        );
    }


    /**
     * 게시글 업데이트
     * @param postId
     * @param postRequestDto
     * @param user
     * @param imgPaths
     * @return ApiResponseDto<PostResponseDto>
     */
    @Transactional
    public ApiResponseDto<PostResponseDto> updatePost(Long postId,
                                                   PostRequestDto postRequestDto,
                                                   User user,
                                                   List<String> imgPaths) {

        Post post = entityValidatorManager.validatePost(postId);
        validatePostUser(post, user);

        //저장된 이미지 리스트 가져오기
        List<String> newImgList = imageUploaderManager.updateImage(imgPaths, post);


        post.update(postRequestDto);
        return ApiResponseDto.success(
                SUCCESS_POST_EDIT.getMessage(),
                PostResponseDto.fromEntity(post, newImgList)
        );
    }

    /**
     * 게시글 삭제
     * @param postId
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> deletePost(Long postId, User user) {

        Post post = entityValidatorManager.validatePost(postId);
        validatePostUser(post, user);

        postRepository.delete(post);
        List<String> imgList = imageUploaderManager.getListImage(post);
        imageUploaderManager.deleteImageList(post, imgList);
        return ApiResponseDto.success(SUCCESS_POST_DELETE.getMessage());
    }

    /**
     * 게시글 작성자와 로그인한 사용자가 일치하는지 확인
     * @param post
     * @param user
     */
    public void validatePostUser(@NotNull Post post, User user) {
        if (post.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_USER_MATCH);
        }
    }

    /**
     * 카테고리 검증
     * @param category
     */
    public void validateCategory(PostCategory category) {

        if (!PostCategory.isValidCategory(category)) {
            log.error("[FAIL] {} 카테고리가 존재하지 않습니다.", category);
            throw new CustomException(ErrorCode.POST_CATEGORY_NOT_FOUND);
        }
    }

    /**
     * 조회수 증가
     * @param post
     */
    public void viewCount(Post post) {
        post.increasePostView();
    }



}
