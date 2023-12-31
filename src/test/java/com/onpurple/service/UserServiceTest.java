package com.onpurple.service;

import com.onpurple.domain.user.dto.SignupRequestDto;
import com.onpurple.domain.user.dto.UserInfoRequestDto;
import com.onpurple.domain.user.model.User;
import com.onpurple.domain.user.repository.UserRepository;
import com.onpurple.domain.user.service.UserService;
import com.onpurple.global.enums.ErrorCode;
import com.onpurple.global.exception.CustomException;
import com.onpurple.global.external.AwsS3UploadService;
import com.onpurple.global.redis.cacheRepository.CountCacheRepository;
import com.onpurple.global.security.dto.TokenDto;
import com.onpurple.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AwsS3UploadService awsS3UploadService;

    @Mock
    CountCacheRepository countCacheRepository;
    @Mock
    JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    void test_sign_up_success() {

        //Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .username("회원이름")
                .password("123456789")
                .passwordConfirm("123456789")
                .gender("남자")
                .nickname("회원명닉네임")
                .imageUrl("이미지url")
                .build();

        UserInfoRequestDto userInfoRequestDto = UserInfoRequestDto.builder()
                .age(22)
                .area("부천")
                .mbti("EEEE")
                .smoke("흡연노")
                .drink("음주노")
                .pet("반려동물유")
                .build();

        TokenDto tokenDto = TokenDto.builder()
                .refreshToken("refreshToken")
                .accessToken("accessToken")
                .build();

        String imageUrl = "imageUrl";

        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("123456789");
        when(awsS3UploadService.uploadOne(any())).thenReturn(imageUrl);


        when(jwtTokenProvider.reissueToken(anyString())).thenReturn(tokenDto);
        //When
        userService.createUser(signupRequestDto, userInfoRequestDto, imageUrl);

        //Then
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).save(argThat(userTest ->
                userTest.getUsername().equals("회원이름") &&
                        userTest.getArea().equals("부천") &&
                        userTest.getPassword().equals("123456789")
        ));
        verify(userRepository).save(any(User.class));
    }


    @Test
    @DisplayName("회원가입 실패_비밀번호 확인 불일치")
    void test_password_confirm_not_matched_signUpFailed(){

        //Given
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .username("회원이름")
                .password("123456789")
                .passwordConfirm("123456722")
                .gender("남자")
                .nickname("회원명닉네임")
                .imageUrl("이미지url")
                .build();

        UserInfoRequestDto userInfoRequestDto = UserInfoRequestDto.builder()
                .age(22)
                .area("부천")
                .mbti("EEEE")
                .smoke("흡연노")
                .drink("음주노")
                .pet("반려동물유")
                .build();

        TokenDto tokenDto = TokenDto.builder()
                .refreshToken("refreshToken")
                .accessToken("accessToken")
                .build();

        String imageUrl = "imageUrl";


        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("123456789");
        // When
        when(awsS3UploadService.uploadOne(any())).thenReturn(imageUrl);
        when(jwtTokenProvider.reissueToken(anyString())).thenReturn(tokenDto);


        Throwable exception = assertThrows(CustomException.class, () -> userService.createUser(signupRequestDto, userInfoRequestDto, imageUrl));
        // Then
        assertEquals(ErrorCode.PASSWORD_CONFIRM_NOT_MATCHED.getMessage(), exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }



}