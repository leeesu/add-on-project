package com.onpurple.service;

import com.onpurple.domain.like.model.UnLike;
import com.onpurple.domain.like.repository.UnLikeRepository;
import com.onpurple.domain.like.service.UnLikeService;
import com.onpurple.domain.user.model.User;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.enums.ErrorCode;
import com.onpurple.global.enums.SuccessCode;
import com.onpurple.global.exception.CustomException;
import com.onpurple.global.helper.EntityValidatorManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UnLikeServiceTest {

    @Mock
    EntityValidatorManager entityValidatorManager;

    @Mock
    UnLikeRepository unLikeRepository;
    @InjectMocks
    UnLikeService unLikeService;

    @Test
    @DisplayName("한 번 눌렀을 때 좋아요")
    void first_user_un_like() {
        // given
        User user = mock(User.class);
        User targetUser = mock(User.class);

        given(entityValidatorManager.validateProfile(any())).willReturn(targetUser);
        given(unLikeRepository.findByUserAndTargetId(any(), any())).willReturn(Optional.empty());

        // when
        ApiResponseDto<MessageResponseDto> responseDto = unLikeService.userUnLike(targetUser.getId(), user);

        // then
        assertEquals(SuccessCode.SUCCESS_UN_LIKE.getMessage(), responseDto.getMessage());
    }

    @Test
    @DisplayName("한 번 더 눌러서 싫어요 취소시키기")
    void cancel_user_un_like() {
        // given
        User user = mock(User.class);
        User targetUser = mock(User.class);
        UnLike userUnLike = UnLike.builder().user(user).target(targetUser).build();

        given(entityValidatorManager.validateProfile(any())).willReturn(targetUser);
        given(unLikeRepository.findByUserAndTargetId(any(), any())).willReturn(Optional.of(userUnLike));

        // when
        ApiResponseDto<MessageResponseDto> responseDto = unLikeService.userUnLike(targetUser.getId(), user);

        // then
        assertEquals(SuccessCode.SUCCESS_UN_LIKE_CANCEL.getMessage(), responseDto.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 회원에 싫어요를 누를 때")
    void test_un_like_user_That_is_not_exist() {
        // given

        User targetUser = mock(User.class);

        lenient().when(entityValidatorManager.validateProfile(2L)).thenThrow(new CustomException(ErrorCode.PROFILE_NOT_FOUND));
        // when&then
        Throwable exception = assertThrows(CustomException.class, () -> unLikeService.userUnLike(2L,targetUser));
        // Then
        assertEquals(ErrorCode.PROFILE_NOT_FOUND.getMessage(), exception.getMessage());
    }

}