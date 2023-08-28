package com.onpurple.service;


import com.onpurple.dto.response.*;
import com.onpurple.model.*;
import com.onpurple.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UnLikeRepository unLikeRepository;
    private final UserRepository userRepository;

    // 게시글 좋아요
    @Transactional
    public ResponseDto<?> PostLike(Long postId,
                                   User user) {

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
        }

        if (!post.validateUser(user)) {
            return ResponseDto.fail("BAD_REQUEST", "본인에게 좋아요 할 수 없습니다.");
        }
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

        Comment comment = isPresentComment(commentId);
        if (null == comment)
            return ResponseDto.fail("COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다.");

        if (!comment.validateUser(user)) {
            return ResponseDto.fail("BAD_REQUEST", "본인에게 좋아요 할 수 없습니다.");
        }

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

        User target = isPresentTarget(targetId);
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


        User target = isPresentTarget(targetId);
        if (null == target)
            return ResponseDto.fail("PROFILE_NOT_FOUND", "타겟 유저를 찾을 수 없습니다.");


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
        //매칭되는 아이디 찾아서 가져오기
        //stream 으로 중복제거
        if ((likeList.isEmpty())) {
            return ResponseDto.fail("MATCHING_USER_NOT_FOUND", "매칭된 회원을 찾을 수 없습니다.");
        }

        List<User> getLikeUser = userRepository.matchingUser(likeList);
        List<UserResponseDto> userResponseDtos = new ArrayList<>();

        for (User list : getLikeUser) {
            userResponseDtos.add(
                    UserResponseDto.builder()
                            .userId(list.getId())
                            .nickname(list.getNickname())
                            .imageUrl(list.getImageUrl())
                            .build()
            );


        }
        return ResponseDto.success(userResponseDtos);
    }

    //    내가 좋아요 한 사람 리스트 조회.
//    프론트에서 나를 좋아요 한 사람을 찾아 프로필을 불러올 때 조건을 걸기 위해서 생성된 메소드.
    @Transactional(readOnly = true)
    public ResponseDto<?> getLike(User user) {

        //    토큰을 통해 user를 확인하고 확인된 유저 기준 좋아요를 누른 대상 모드를 찾아 리스트에 저장.
        List<Likes> likesList = likeRepository.findAllByUser(user);
        List<LikesResponseDto> likesResponseDtoList = new ArrayList<>();
        for (Likes likes : likesList) {
            likesResponseDtoList.add(
                    LikesResponseDto.builder()
                            .userId(likes.getTarget().getId())
                            .imageUrl(likes.getTarget().getImageUrl())
                            .build());
        }
        return ResponseDto.success(likesResponseDtoList);
    }

    //    내가 싫어요 한 사람 리스트 조회.
//    264~295의 내가 좋아요한  사람 리스트 조회와 동일한 로직으로 구현.
    @Transactional(readOnly = true)
    public ResponseDto<?> getUnLike(User user) {

        List<UnLike> unLikesList = unLikeRepository.findAllByUser(user);
        List<UnLikesResponseDto> unLikesResponseDtoList = new ArrayList<>();
        for (UnLike unLike : unLikesList) {
            unLikesResponseDtoList.add(
                    UnLikesResponseDto.builder()
                            .userId(unLike.getTarget().getId())
                            .imageUrl(unLike.getTarget().getImageUrl())
                            .build());
        }
        return ResponseDto.success(unLikesResponseDtoList);
    }

    @Transactional(readOnly = true)
    public User isPresentTarget(Long targetId) {
        Optional<User> optionalTarget = userRepository.findById(targetId);
        return optionalTarget.orElse(null);
    }

    @Transactional(readOnly = true)
    public Post isPresentPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
    }

    @Transactional(readOnly = true)
    public Comment isPresentComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        return optionalComment.orElse(null);
    }

}
