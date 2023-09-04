package com.onpurple.service;


import com.onpurple.dto.response.*;
import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.model.*;
import com.onpurple.repository.*;
import com.onpurple.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UnLikeRepository unLikeRepository;
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;

    // 게시글 좋아요
    @Transactional
    public ResponseDto<?> PostLike(Long postId,
                                   User user) {

        Post post = validationUtil.assertValidatePost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
        }
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
            post.addLike();
            return ResponseDto.success(
                    LikeResponseDto.builder()
                            .likes(postLike.getPost().getLikes())
                            .build()
            );
        } else {
            likeRepository.delete(liked);
            post.minusLike();
            return ResponseDto.success(false);

        }
    }

    // 댓글 좋아요
    @Transactional
    public ResponseDto<?> CommentLike(Long commentId,
                                      User user) {
        // 댓글 유효성 체크
        Comment comment = validationUtil.assertValidateComment(commentId);
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
            return ResponseDto.success(
                    LikeResponseDto.builder()
                            .likes(commentLike.getComment().getLikes()).build()
            );
        } else {
            likeRepository.delete(liked);
            comment.minusLike();
            return ResponseDto.success(false);
        }
    }

    //회원 좋아요
    @Transactional
    public ResponseDto<?> UserLike(Long targetId,
                                   User user) {

        User target = validationUtil.assertValidateUser(targetId);
        if (null == target)
            return ResponseDto.fail("PROFILE_NOT_FOUND", "타겟 유저를 찾을 수 없습니다.");


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
            return ResponseDto.success("좋아요 성공");
        } else {
            likeRepository.delete(liked);
            int cancelLike = likeRepository.countByTargetId(targetId);
            target.minusLike(cancelLike);
            return ResponseDto.success("좋아요가 취소되었습니다.");
        }
    }


    //회원 싫어요
    public ResponseDto<?> ProfileUnLike(Long targetId,
                                        User user) {


        User target = validationUtil.assertValidateUser(targetId);

        //좋아요 한 적 있는지 체크
        UnLike unLiked = unLikeRepository.findByUserAndTargetId(user, targetId).orElse(null);

        if (unLiked == null) {
            UnLike userUnLike = UnLike.builder()
                    .user(user)
                    .target(target)
                    .build();
            unLikeRepository.save(userUnLike);
            int addUnLike = unLikeRepository.countByTargetId(targetId);
            log.info("지금 싫어요 수 : "+addUnLike);
            target.addUnLike(addUnLike);
            return ResponseDto.success("싫어요 성공");
        } else {
            unLikeRepository.delete(unLiked);
            int cancelUnLike = unLikeRepository.countByTargetId(targetId);
            log.info("지금 싫어요 수 : "+cancelUnLike);
            target.minusUnLike(cancelUnLike);
            return ResponseDto.success("싫어요가 취소되었습니다.");
        }
    }

    // 매칭 JPQL QUERY방식
    @Transactional(readOnly = true)
    public ResponseDto<?> likeCheck(Long userId, User user) {
        List<Integer> likeList = likeRepository.likeToLikeUserId(userId)
                .stream()
                .distinct()
                .collect(Collectors.toList());

        if (likeList.isEmpty()) {
            return ResponseDto.fail("MATCHING_USER_NOT_FOUND", "MATCHING_USER_NOT_FOUND");
        }

        List<UserResponseDto> userResponseDto = userRepository.matchingUser(likeList)
                .stream()
                .map(UserResponseDto::createFromEntity)
                .collect(Collectors.toList());

        return ResponseDto.success(userResponseDto);
    }


    //    내가 좋아요 한 사람 리스트 조회.
    //    프론트에서 나를 좋아요 한 사람을 찾아 프로필을 불러올 때 조건을 걸기 위해서 생성된 메소드.
    @Transactional(readOnly = true)
    public ResponseDto<?> getLike(User user) {
        //    토큰을 통해 user를 확인하고 확인된 유저 기준 좋아요를 누른 대상 모드를 찾아 리스트에 저장
        List<Likes> likesList = likeRepository.findAllByUser(user);

        List<LikesResponseDto> likesResponseDtoList = likesList
                .stream()
                .map(LikesResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseDto.success(likesResponseDtoList);
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

}
