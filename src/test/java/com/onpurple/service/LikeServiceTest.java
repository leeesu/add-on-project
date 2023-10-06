package com.onpurple.service;

import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.LikeResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.enums.ErrorCode;
import com.onpurple.enums.SuccessCode;
import com.onpurple.exception.CustomException;
import com.onpurple.model.Comment;
import com.onpurple.model.Likes;
import com.onpurple.model.Post;
import com.onpurple.model.User;
import com.onpurple.repository.LikeRepository;
import com.onpurple.helper.EntityValidatorManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    LikeRepository likeRepository;

    @Mock
    EntityValidatorManager entityValidatorManager;

    @InjectMocks
    LikeService likeService;

    @DisplayName("게시글 좋아요")
    @Nested
    class LikePost {

        @Test
        @DisplayName("처음 누를 때")
        void first_post_like() {
            User author = mock(User.class);
            // given
            Post post = Post.builder()
                    .title("제목")
                    .likes(0)
                    .user(author)
                    .content("내용")
                    .imageUrl("이미지")
                    .build();
            User user = mock(User.class);

            given(entityValidatorManager.validatePost(any())).willReturn(post);
            given(likeRepository.findByUserAndPostId(any(), any())).willReturn(Optional.empty());

            // when
            ApiResponseDto<LikeResponseDto> responseDto = likeService.postLike(post.getId(), user);

            // then
            assertEquals(SuccessCode.SUCCESS_POST_LIKE.getMessage(), responseDto.getMessage());
            assertEquals(post.getLikes(), 1);
            System.out.println("현재 좋아요 수 : " + 1);
        }

        @Test
        @DisplayName("좋아요 취소하기")
        void cancel_post_like_한번_더_눌러서() {
            // given
            Post post = mock(Post.class);
            User user = mock(User.class);
            Likes postLike = Likes.builder().user(user).post(post).build();

            given(entityValidatorManager.validatePost(any())).willReturn(post);
            given(likeRepository.findByUserAndPostId(any(), any())).willReturn(Optional.of(postLike));
            when(post.validateUser(any())).thenReturn(true);

            // when
            ApiResponseDto<LikeResponseDto> responseDto = likeService.postLike(post.getId(), user);

            // then
            assertEquals(SuccessCode.SUCCESS_POST_LIKE_CANCEL.getMessage(), responseDto.getMessage());;
        }

        @Test
        @DisplayName("존재하지 않는 게시글에 좋아요를 누를 때")
        void test_like_post_That_is_not_exist() {
            // given
            Post post = mock(Post.class);
            User user = mock(User.class);
            Likes postLike = Likes.builder().user(user).post(post).build();

            lenient().when(entityValidatorManager.validatePost(2L)).thenThrow(new CustomException(ErrorCode.POST_NOT_FOUND));
            // when&then
            Throwable exception = assertThrows(CustomException.class, () -> likeService.postLike(2L, user));
            // Then
            assertEquals(ErrorCode.POST_NOT_FOUND.getMessage(), exception.getMessage());
        }
    }

    @DisplayName("댓글 좋아요")
    @Nested
    class LikeComment {
        @Test
        @DisplayName("처음 누를 때")
        void first_comment_like() {
            // given
            User user = mock(User.class);
            Comment comment = mock(Comment.class);

            given(entityValidatorManager.validateComment(any())).willReturn(comment);
            given(likeRepository.findByUserAndCommentId(any(), any())).willReturn(Optional.empty());
            when(comment.validateUser(any())).thenReturn(true);

            // when
            ApiResponseDto<LikeResponseDto> responseDto = likeService.commentLike(comment.getId(), user);

            // then
            assertEquals(SuccessCode.SUCCESS_COMMENT_LIKE.getMessage(), responseDto.getMessage());
        }

        @Test
        @DisplayName("좋아요 취소하기")
        void cancel_comment_like_한번_더_눌러서() {
            // given
            User user = mock(User.class);
            Comment comment = mock(Comment.class);
            Likes commentLike = Likes.builder().user(user).comment(comment).build();

            given(entityValidatorManager.validateComment(any())).willReturn(comment);
            given(likeRepository.findByUserAndCommentId(any(), any())).willReturn(Optional.of(commentLike));
            when(comment.validateUser(any())).thenReturn(true);

            // when
            ApiResponseDto<LikeResponseDto> responseDto = likeService.commentLike(comment.getId(), user);

            // then
            assertEquals(SuccessCode.SUCCESS_COMMENT_LIKE_CANCEL.getMessage(), responseDto.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 댓글에 좋아요를 누를 때")
        void test_like_comment_that_is_not_exist() {
            // given
            Comment comment = mock(Comment.class);
            User user = mock(User.class);
            Likes postLike = Likes.builder().user(user).comment(comment).build();

            lenient().when(entityValidatorManager.validateComment(2L)).thenThrow(new CustomException(ErrorCode.COMMENT_NOT_FOUND));
            // when&then
            Throwable exception = assertThrows(CustomException.class, () -> likeService.commentLike(2L, user));
            // Then
            assertEquals(ErrorCode.COMMENT_NOT_FOUND.getMessage(), exception.getMessage());
        }
    }
    @Nested
    @DisplayName("회원 좋아요")
    class LikeUser {
        @Test
        @DisplayName("한 번 눌렀을 때 좋아요")
        void first_user_like() {
            // given
            User user = mock(User.class);
            User targetUser = mock(User.class);

            given(entityValidatorManager.validateProfile(any())).willReturn(targetUser);
            given(likeRepository.findByUserAndTargetId(any(), any())).willReturn(Optional.empty());

            // when
            ApiResponseDto<MessageResponseDto> responseDto = likeService.userLike(targetUser.getId(), user);

            // then
            assertEquals(SuccessCode.SUCCESS_USER_LIKE.getMessage(), responseDto.getMessage());
        }

        @Test
        @DisplayName("한 번 더 눌러서 좋아요 취소시키기")
        void cancel_user_like() {
            // given
            User user = mock(User.class);
            User targetUser = mock(User.class);
            Likes userLike = Likes.builder().user(user).target(targetUser).build();

            given(entityValidatorManager.validateProfile(any())).willReturn(user);
            given(likeRepository.findByUserAndTargetId(any(), any())).willReturn(Optional.of(userLike));

            // when
            ApiResponseDto<MessageResponseDto> responseDto = likeService.userLike(user.getId(), targetUser);

            // then
            assertEquals(SuccessCode.SUCCESS_USER_LIKE_CANCEL.getMessage(), responseDto.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 회원에 좋아요를 누를 때")
        void test_like_user_That_is_not_exist() {
            // given

            User user = mock(User.class);

            lenient().when(entityValidatorManager.validateProfile(2L)).thenThrow(new CustomException(ErrorCode.PROFILE_NOT_FOUND));
            // when&then
            Throwable exception = assertThrows(CustomException.class, () -> likeService.userLike(2L, user));
            // Then
            assertEquals(ErrorCode.PROFILE_NOT_FOUND.getMessage(), exception.getMessage());
        }


    }
    @Nested
    class MultiLike {
        @Test
        @DisplayName("100명이 동시에 좋아요 누르는 경우")
        void multi_post_like() throws InterruptedException {
            User author = mock(User.class);
            // given
            Post post = Post.builder()
                    .title("제목")
                    .likes(0)
                    .user(author)
                    .content("내용")
                    .imageUrl("이미지")
                    .build();

            given(entityValidatorManager.validatePost(any())).willReturn(post);

            int threadCount = 100;
            ExecutorService executorService = Executors.newFixedThreadPool(100);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                User user = mock(User.class); // 각각의 스레드가 다른 유저로부터의 좋아요라고 가정합니다.

                executorService.execute(() -> {
                    likeService.postLike(post.getId(), user);
                    latch.countDown();
                });
            }

            latch.await(); // 모든 작업이 끝날 때까지 기다립니다.

            // then
            System.out.println("현재 좋아요 수 : " + post.getLikes());
        }


        @Test
        @DisplayName("40명이 동시에 좋아요 누르는 경우")
        void multi_post_like_two() throws InterruptedException {
            User author = mock(User.class);
            // given
            Post post = Post.builder()
                    .title("제목")
                    .likes(0)
                    .user(author)
                    .content("내용")
                    .imageUrl("이미지")
                    .build();


            given(entityValidatorManager.validatePost(any())).willReturn(post);

            int threadCount = 40;
            ExecutorService executorService = Executors.newFixedThreadPool(40);
            CountDownLatch latch = new CountDownLatch(threadCount);


            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                            try {
                                User user = mock(User.class);
                                likeService.postLike(post.getId(), user);
                            } finally {
                                latch.countDown();
                            }
                        }
                );
            }


            latch.await(); // 모든 작업이 끝날 때까지 기다립니다.
            // then
            System.out.println("현재 좋아요 수 : " + post.getLikes());

        }
    }


}