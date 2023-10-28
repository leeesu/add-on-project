package com.onpurple.service;

import com.onpurple.dto.request.*;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.UserResponseDto;
import com.onpurple.enums.ErrorCode;
import com.onpurple.enums.SuccessCode;
import com.onpurple.exception.CustomException;
import com.onpurple.model.Authority;
import com.onpurple.model.User;
import com.onpurple.repository.UserRepository;
import com.onpurple.security.jwt.JwtTokenProvider;
import com.onpurple.redis.repository.RefreshTokenRepository;
import com.onpurple.external.s3.AwsS3UploadService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.onpurple.enums.RedisKeyEnum.*;
import static com.onpurple.enums.SuccessCode.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3UploadService awsS3UploadService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String ADMIN_TOKEN = ("AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC");

    /**
     * 아이디 체크
     * @param username
     * @return ApiResponseDto<MessageResponseDto>
     * 아이디 체크. DB에 저장되어 있는 usernaeme을 찾아 유저가 존재한다면 에러메시지 전송
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> checkUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (null != isPresentUser(username))
            throw new CustomException(ErrorCode.DUPLICATED_USERNAME);
        return ApiResponseDto.success("사용 가능한 ID입니다.");
    }

    /**
     * 닉네임 체크
     * @param nickname
     * @return ApiResponseDto<MessageResponseDto>
     * DB에 저장되어 있는 usernaeme을 찾아 유저가 존재한다면 에러메시지 전송
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> checkNickname(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        if (null != isPresentNickname(nickname))
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        return ApiResponseDto.success(SuccessCode.SUCCESS_NICKNAME_CHANGE.getMessage());
    }

    /**
     * 회원가입
     * @param requestDto
     * @param userInfoRequestDto
     * @param imgPaths
     * @return ApiResponseDto<UserResponseDto>
     */
    @Transactional
    public ApiResponseDto<UserResponseDto> createUser(SignupRequestDto requestDto, UserInfoRequestDto userInfoRequestDto,
                                                      String imgPaths) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCHED);
        }
//     역할 확인. requestDto에 따라 아무 설정도 하지 않을 경우 자동으로 역할이 user로 고정.
//     관리자의 경우 isAdmin이 true여야하고 adminToken을 입력하여 일치할 경우 회원가입 완료시에 역할이 Admin으로 반환
        Authority role = Authority.USER;
        if (requestDto.isAdmin()) {
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new CustomException(ErrorCode.ADMIN_PASSWORD_NOT_MATCHED);

            }
            role = Authority.ADMIN;
        }

        User user = User.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .gender(requestDto.getGender())
                .imageUrl(imgPaths)
                .role(role)
                .age(userInfoRequestDto.getAge())
                .mbti(userInfoRequestDto.getMbti())
                .introduction(userInfoRequestDto.getIntroduction())
                .area(userInfoRequestDto.getArea())
                .idealType(userInfoRequestDto.getIdealType())
                .job(userInfoRequestDto.getJob())
                .hobby(userInfoRequestDto.getHobby())
                .smoke(userInfoRequestDto.getSmoke())
                .drink(userInfoRequestDto.getDrink())
                .likeMovieType(userInfoRequestDto.getLikeMovieType())
                .pet(userInfoRequestDto.getPet())
                .build();
        userRepository.save(user);


//        현재 서비스에서 회원가입 이후 바로 서비스를 이용할 수 있도록 설정하였기에 회원가입이 진행될 때 토큰이 발행되도록 설정
        jwtTokenProvider.reissueToken(user.getUsername());

        if (user.getRole().equals(Authority.ADMIN)) {
            return ApiResponseDto.success(SUCCESS_ADMIN_SIGNUP.getMessage());
        }

        return ApiResponseDto.success(
                SUCCESS_SIGNUP.getMessage(),
                UserResponseDto.createFromEntity(user));

    }

    /**
     * 내 정보 조회
     * @param user
     * @return ApiResponseDto<UserResponseDto>
     */

    @Transactional
    public ApiResponseDto<UserResponseDto> getUser(User user) {

        return ApiResponseDto.success(
                SUCCESS_GET_USER.getMessage(),
                UserResponseDto.getFromEntity(user)
        );
    }

    /**
     * 비밀번호 수정
     * @param requestDto
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> updatePassword(UserUpdateRequestDto requestDto, User user) {

        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCHED);
        }

//  변경 비밀번호를 입력하여 정상처리될 경우 비밀번호 업데이트 실행. 이후 password를 set을 이용하여 복호화 적용.
        user.update(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);

        return ApiResponseDto.success(
                SUCCESS_PASSWORD_CHANGE.getMessage());
    }

    /**
     * 이미지 수정
     * @param user
     * @param imgPaths
     * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    @CachePut(value = "user", key = "#user.id")
    public ApiResponseDto<MessageResponseDto> updateImage(User user, String imgPaths) {

//        이미지를 확인하고 user의 기존 이미지를 삭제. 이후 새로 넣은 이미지로 업데이트 되도록 설정.
        if (imgPaths != null) {
            String deleteImage = user.getImageUrl();
            awsS3UploadService.deleteFile(AwsS3UploadService.getFileNameFromURL(deleteImage));
        }
//        수정된 이미지를 imgList의 첫번째 배열에 저장한 후 user에 저장.
        user.imageSave(imgPaths);

        return ApiResponseDto.success(SUCCESS_PROFILE_IMG_UPDATE.getMessage());
    }

    //  로그아웃. 토큰을 확인하여 일치할 경우 로그인 된 유저의 이미지와 토큰을 삭제.

    /**
     * 로그아웃
     * @param request
     * @param response
     * @return ApiResponseDto<MessageResponseDto>
     */
    public ApiResponseDto<MessageResponseDto> logout(HttpServletRequest request, HttpServletResponse response) {
        // 리프레시 토큰 삭제, AccessToken 만료시간까지 저장
        jwtTokenProvider.logoutBlackListToken(request);
        // 쿠키에서 JWT 삭제
        jwtTokenProvider.deleteJwtFromCookie(response);

        return ApiResponseDto.success(SuccessCode.SUCCESS_LOGOUT.getMessage());
    }


    /**
     * 회원명 중복확인
     * @param username
     * @return
     */
    public User isPresentUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

    /**
     * 닉네임 중복확인
     * @param nickname
     * @return
     */
    public User isPresentNickname(String nickname) {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        return optionalUser.orElse(null);
    }

}
