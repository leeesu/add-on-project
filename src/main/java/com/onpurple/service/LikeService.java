package com.onpurple.service;


import com.onpurple.dto.response.*;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.*;
import com.onpurple.repository.*;
import com.onpurple.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.onpurple.enums.SuccessCode.*;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;


    /*
    * 게시글 좋아요
    * @param postId, user
    * @return ApiResponseDto<LikeResponseDto>
     */
    @Transactional
    public ApiResponseDto<LikeResponseDto> postLike(Long postId,
                                                    User user) {
        // 게시글 유효성 체크
        Post post = validationUtil.validatePost(postId);
        // 본인에게 좋아요 할 수 없도록 예외처리
        validatePostLikeUser(post, user);
        //좋아요 한 적 있는지 체크
        Likes liked = likeRepository.findByUserAndPostId(user, postId).orElse(null);

        if (liked == null) {
            Likes postLike = Likes.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(postLike);
            addLikeCount(post);
            return ApiResponseDto.success(SUCCESS_POST_LIKE.getMessage(),
                    LikeResponseDto.fromPostLikesEntity(postLike)
            );
        } else {
            likeRepository.delete(liked);
            post.minusLike();
            return ApiResponseDto.success(SUCCESS_POST_LIKE_CANCEL.getMessage());

        }
    }

    /*
    * 댓글 좋아요
    * @param commentId, user
    * @return ApiResponseDto<LikeResponseDto>
     */

    @Transactional
    public ApiResponseDto<LikeResponseDto> commentLike(Long commentId,
                                                       User user) {
        // 댓글 유효성 체크
        Comment comment = validationUtil.validateComment(commentId);
        // 본인 댓글에 좋아요 할 수 없도록 예외처리
        validateCommentLikeUser(comment, user);

        //좋아요 한 적 있는지 체크
        Likes liked = likeRepository.findByUserAndCommentId(user, commentId).orElse(null);

        if (liked == null) {
            Likes commentLike = Likes.builder()
                    .user(user)
                    .comment(comment)
                    .build();
            likeRepository.save(commentLike);
            comment.addLike();
            return ApiResponseDto.success(
                    SUCCESS_COMMENT_LIKE.getMessage(),
                    LikeResponseDto.fromCommentLikesEntity(commentLike));
        } else {
            likeRepository.delete(liked);
            comment.minusLike();
            return ApiResponseDto.success(SUCCESS_COMMENT_LIKE_CANCEL.getMessage());
        }
    }


    /*
    * 회원 좋아요
    * @param targetId, user
    * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> userLike(Long targetId,
                                                       User user) {

        User target = validationUtil.validateProfile(targetId);

        //좋아요 한 적 있는지 체크
        Likes liked = likeRepository.findByUserAndTargetId(user, targetId).orElse(null);

        if (liked == null) {
            Likes userLike = Likes.builder()
                    .user(user)
                    .target(target)
                    .build();
            likeRepository.save(userLike);
            int addLike = likeRepository.countByTargetId(targetId);
            target.addLike(addLike);
            return ApiResponseDto.success(SUCCESS_USER_LIKE.getMessage());
        } else {
            likeRepository.delete(liked);
            int cancelLike = likeRepository.countByTargetId(targetId);
            target.minusLike(cancelLike);
            return ApiResponseDto.success(SUCCESS_USER_LIKE_CANCEL.getMessage());
        }
    }

    // 매칭 JPQL QUERY방식
    /*
    * 매칭 조회
    * @param userId, user
    * @return ApiResponseDto<List<UserResponseDto>>
     */
    @Transactional(readOnly = true)
    public ApiResponseDto<List<UserResponseDto>> likeCheck(Long userId, User user) {
        List<Integer> likeList = likeRepository.likeToLikeUserId(userId)
                .stream()
                .distinct()
                .collect(Collectors.toList());

        if (likeList.isEmpty()) {
            throw new CustomException(ErrorCode.MATCHING_NOT_FOUND);
        }

        List<UserResponseDto> userResponseDto = userRepository.matchingUser(likeList)
                .stream()
                .map(UserResponseDto::createFromEntity)
                .collect(Collectors.toList());

        return ApiResponseDto.success(SUCCESS_MATCHING_FOUND.getMessage(),userResponseDto);
    }


    //    내가 좋아요 한 사람 리스트 조회.
    //    프론트에서 나를 좋아요 한 사람을 찾아 프로필을 불러올 때 조건을 걸기 위해서 생성된 메소드.
    @Transactional(readOnly = true)
    public ApiResponseDto<List<LikesResponseDto>> getLike(User user) {
        //    토큰을 통해 user를 확인하고 확인된 유저 기준 좋아요를 누른 대상 모드를 찾아 리스트에 저장
        List<Likes> likesList = likeRepository.findAllByUser(user);
        if(likesList.isEmpty()){
            throw new CustomException(ErrorCode.LIKE_ME_USER_NOT_FOUND);
        }

        List<LikesResponseDto> likesResponseDtoList = likesList
                .stream()
                .map(LikesResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ApiResponseDto.success(SUCCESS_LIKE_USER_FOUND.getMessage(),likesResponseDtoList);
    }



    public void validatePostLikeUser(Post post, User user) {
        if (!post.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_SELF_LIKE);
        }
    }

    public void validateCommentLikeUser(Comment comment, User user) {

        if (!comment.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_SELF_LIKE);
        }
    }

    public void addLikeCount(Post post) {
        post.addLike();
    }

}
