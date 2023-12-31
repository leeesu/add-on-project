package com.onpurple.service;

import com.onpurple.domain.comment.dto.CommentRequestDto;
import com.onpurple.domain.comment.dto.CommentResponseDto;
import com.onpurple.domain.comment.model.Comment;
import com.onpurple.domain.comment.repository.CommentRepository;
import com.onpurple.domain.comment.service.CommentService;
import com.onpurple.domain.notification.helper.NotificationRequestManager;
import com.onpurple.domain.post.model.Post;
import com.onpurple.domain.user.model.User;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.enums.SuccessCode;
import com.onpurple.global.helper.EntityValidatorManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {


    @Mock
    EntityValidatorManager entityValidatorManager;

    @Mock
    CommentRepository commentRepository;
    @Mock
    NotificationRequestManager notificationRequestManager;

    @InjectMocks
    CommentService commentService;

    final User user = mock(User.class);
    final Post post = mock(Post.class);
    final CommentRequestDto requestDto = CommentRequestDto.builder()
            .postId(post.getId())
            .comment("댓글")
            .build();

    final Comment defaultComment = Comment.builder()
            .post(post)
            .comment(requestDto.getComment())
            .user(user)
            .build();

    @Test
    @DisplayName("댓글 작성")
    void create_comment() {
        // given
        given(entityValidatorManager.validatePost(any())).willReturn(post);
        given(commentRepository.save(any())).willReturn(defaultComment);
        given(post.getUser()).willReturn(mock(User.class));  // post의 사용자와 댓글 작성자가 다른 상황을 가정

        // when
        ApiResponseDto<CommentResponseDto> saveComment = commentService.createComment(post.getId(),requestDto, user);

        // then
        assertThat(saveComment.getData().getComment()).isEqualTo(requestDto.getComment());
        // 알림이 제대로 보내졌는지 검증
        verify(notificationRequestManager).sendCommentNotification(post, user);
    }

    @Test
    @DisplayName("댓글 전체 조회하기")
    void get_all_comment_by_post() {
        // given
        given(entityValidatorManager.validatePost(any())).willReturn(post);

        // when
        List<Comment> mockCommentList = new ArrayList<>();
        for(int i=0; i < 10; i++) {
            Comment mockComment = mock(Comment.class);

            when(user.getNickname()).thenReturn("Mock User ");
            when(mockComment.getComment()).thenReturn("Mock Comment");
            when(mockComment.getUser()).thenReturn(user);

            mockCommentList.add(mockComment);
        }

        when(commentRepository.findAllByPost(any())).thenReturn(mockCommentList);

        ApiResponseDto<List<CommentResponseDto>> responseDto = commentService.getAllCommentsByPost(post.getId());

        //then
        assertThat(responseDto.getData().size()).isEqualTo(mockCommentList.size());
    }

    @Test
    @DisplayName("댓글 수정")
    void update_comment() {
        CommentRequestDto updateRequestDto = CommentRequestDto.builder()
                .comment("댓글내용 수정")
                .build();
        // given
        given(entityValidatorManager.validateComment(any())).willReturn(defaultComment);
        // when
        ApiResponseDto<CommentResponseDto> updateComment = commentService.updateComment(defaultComment.getId(), updateRequestDto, user);
        // then
        assertThat(updateComment.getData().getComment()).isEqualTo(updateRequestDto.getComment());
    }


    @Test
    @DisplayName("댓글 삭제")
    void delete_comment() {
        // given
        given(entityValidatorManager.validateComment(any())).willReturn(defaultComment);
        // when
        ApiResponseDto<MessageResponseDto> deleteComment = commentService.deleteComment(defaultComment.getId(), user);
        // then
        assertThat(deleteComment.getMessage()).isEqualTo(SuccessCode.SUCCESS_COMMENT_DELETE.getMessage());
    }

}