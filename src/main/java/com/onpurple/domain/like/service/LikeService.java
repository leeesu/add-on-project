package com.onpurple.domain.like.service;


import com.onpurple.domain.comment.model.Comment;
import com.onpurple.domain.like.dto.LikeResponseDto;
import com.onpurple.domain.like.dto.LikesResponseDto;
import com.onpurple.domain.like.model.Likes;
import com.onpurple.domain.like.repository.LikeRepository;
import com.onpurple.domain.post.model.Post;
import com.onpurple.domain.user.dto.UserResponseDto;
import com.onpurple.domain.user.model.User;
import com.onpurple.domain.user.repository.UserRepository;
import com.onpurple.global.config.aop.DistributedLock;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.exception.CustomException;
import com.onpurple.global.enums.ErrorCode;
import com.onpurple.global.helper.EntityValidatorManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.onpurple.global.enums.SuccessCode.*;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final EntityValidatorManager entityValidatorManager;

    /**
     *  게시글 좋아요
     * @DistributedLock 분산 Lock
     * @param postId
     * @param user
     * @return ApiResponseDto<LikeResponseDto>
     */
    @DistributedLock
    @Transactional
    public ApiResponseDto<LikeResponseDto> postLike(Long postId,
                                                    User user) {
        // 게시글 유효성 체크 & 분산 Lock
        Post post = entityValidatorManager.validatePost(postId);
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
            post.increasePostLike();
            return ApiResponseDto.success(SUCCESS_POST_LIKE.getMessage(),
                    LikeResponseDto.fromPostLikesEntity(postLike)
            );
        } else {
            likeRepository.delete(liked);
            post.cancelPostLike();
            return ApiResponseDto.success(SUCCESS_POST_LIKE_CANCEL.getMessage());

        }
    }


    /**
     * 댓글 좋아요
     * @DistributedLock 분산 Lock
     * @param commentId
     * @param user
     * @return ApiResponseDto<LikeResponseDto>
     */
    @DistributedLock
    @Transactional
    public ApiResponseDto<LikeResponseDto> commentLike(Long commentId,
                                                       User user) {
        // 댓글 유효성 체크
        Comment comment = entityValidatorManager.validateComment(commentId);
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
            // 댓글 좋아요 수 증가
            comment.increaseCommentLike();
            return ApiResponseDto.success(
                    SUCCESS_COMMENT_LIKE.getMessage(),
                    LikeResponseDto.fromCommentLikesEntity(commentLike));
        } else {
            likeRepository.delete(liked);
            // 댓글 좋아요 수 취소
            comment.cancelCommentLike();
            return ApiResponseDto.success(SUCCESS_COMMENT_LIKE_CANCEL.getMessage());
        }
    }

    /**
     * 회원 좋아요
     * @DistributedLock 분산 Lock
     * @param targetId
     * @param user
     * @return
     */
    @DistributedLock
    @Transactional
    public ApiResponseDto<MessageResponseDto> userLike(Long targetId,
                                                       User user) {

        User target = entityValidatorManager.validateProfile(targetId);

        //좋아요 한 적 있는지 체크
        Likes liked = likeRepository.findByUserAndTargetId(user, targetId).orElse(null);

        if (liked == null) {
            Likes userLike = Likes.builder()
                    .user(user)
                    .target(target)
                    .build();
            likeRepository.save(userLike);
            target.increaseUserLike();
            return ApiResponseDto.success(SUCCESS_USER_LIKE.getMessage());
        } else {
            likeRepository.delete(liked);
            target.cancelUserLike();
            return ApiResponseDto.success(SUCCESS_USER_LIKE_CANCEL.getMessage());
        }
    }

    /**
     * 매칭 조회
     * @param user
     * @return ApiResponseDto<List<UserResponseDto>>
     */
    @Transactional(readOnly = true)
    public ApiResponseDto<List<UserResponseDto>> likeCheck(User user) {
        List<Integer> likeList = likeRepository.likeToLikeUserId(user.getId())
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
    //

    /**
     * 내가 좋아요 한 사람 리스트 조회
     * @param user
     * @return ApiResponseDto<List<LikesResponseDto>>
     */

    //프론트에서 나를 좋아요 한 사람을 찾아 프로필을 불러올 때 조건을 걸기 위해서 생성된 메소드.
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

    /**
     * 본인 게시글에 좋아요 할 수 없도록 예외처리
     * @param post
     * @param user
     */
    public void validatePostLikeUser(Post post, User user) {
        if (!post.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_SELF_LIKE);
        }
    }

    /**
     * 본인 댓글에 좋아요 할 수 없도록 예외처리
     * @param comment
     * @param user
     */
    public void validateCommentLikeUser(Comment comment, User user) {

        if (!comment.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_SELF_LIKE);
        }
    }

}
