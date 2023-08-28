package com.onpurple.service;

import com.onpurple.dto.request.*;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.dto.response.UserResponseDto;
import com.onpurple.model.Authority;
import com.onpurple.model.Img;
import com.onpurple.model.User;
import com.onpurple.repository.ImgRepository;
import com.onpurple.repository.UserRepository;
import com.onpurple.security.jwt.JwtUtil;
import com.onpurple.util.AwsS3UploadService;
import com.onpurple.util.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImgRepository imgRepository;
    private final AwsS3UploadService awsS3UploadService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private static final String ADMIN_TOKEN = ("AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC");

    //    아이디 체크. DB에 저장되어 있는 usernaeme을 찾아 유저가 존재한다면 에러메시지 전송)
    @Transactional
    public ResponseDto<?> checkUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (null != isPresentUser(username))
            return ResponseDto.fail("DUPLICATED_USERNAME", "중복된 ID 입니다.");
        return ResponseDto.success("사용 가능한 ID입니다.");
    }

    //    닉네임 체크. DB에 저장되어 있는 usernaeme을 찾아 유저가 존재한다면 에러메시지 전송)
    @Transactional
    public ResponseDto<?> checkNickname(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        if (null != isPresentNickname(nickname))
            return ResponseDto.fail("DUPLICATED_NICKANAME", "중복된 닉네임 입니다.");
        return ResponseDto.success("사용 가능한 닉네임 입니다.");
    }

    //    회원가입. SingupRequsetDto에 선언한 내용을 입력하여 회원가입
    @Transactional
    public ResponseDto<?> createUser(SignupRequestDto requestDto, UserInfoRequestDto userInfoRequestDto,
                                     List<String> imgPaths, HttpServletResponse response) {
        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            return ResponseDto.fail("PASSWORDS_NOT_MATCHED",
                    "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
//     역할 확인. requestDto에 따라 아무 설정도 하지 않을 경우 자동으로 역할이 user로 고정.
//     관리자의 경우 isAdmin이 true여야하고 adminToken을 입력하여 일치할 경우 회원가입 완료시에 역할이 Admin으로 반환
        Authority role = Authority.USER;
        if (requestDto.isAdmin()) {
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                return ResponseDto.fail("BAD_REQUEST", "관리자 암호가 틀려 등록이 불가합니다.");

            }
            role = Authority.ADMIN;
        }

        User user = User.builder()
                .username(requestDto.getUsername())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .gender(requestDto.getGender())
                .imageUrl(requestDto.getImageUrl())
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

        postBlankCheck(imgPaths);

//        이미지 등록 이미지를 추가하여 user의 imgList 첫번째 배열에 저장
        List<String> imgList = new ArrayList<>();
        for (String imgUrl : imgPaths) {
            Img img = new Img(imgUrl, user);
            imgList.add(img.getImageUrl());
        }
        user.imageSave(imgList.get(0));

//        현재 서비스에서 회원가입 이후 바로 서비스를 이용할 수 있도록 설정하였기에 회원가입이 진행될 때 토큰이 발행되도록 설정
        TokenDto tokenDto = jwtUtil.createAllToken(jwtUtil.createAccessToken(user), jwtUtil.createRefreshToken(user));
        jwtUtil.tokenAddHeaders(tokenDto, response);

        if (user.getRole().equals(Authority.ADMIN)) {
            return ResponseDto.success("관리자 회원가입이 완료되었습니다");
        }

        return ResponseDto.success(
                UserResponseDto.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .imageUrl(user.getImageUrl())
                        .gender(user.getGender())
                        .build()
        );

    }

    private void postBlankCheck(List<String> imgPaths) {
        if (imgPaths == null || imgPaths.isEmpty()) { //.isEmpty()도 되는지 확인해보기
            throw new NullPointerException("이미지를 등록해주세요(Blank Check)");
        }
    }

    @Transactional
    public ResponseDto<?> getUser(User user) {

        return ResponseDto.success(
                UserResponseDto.builder()
                        .userId(user.getId())
                        .nickname(user.getNickname())
                        .gender((user.getGender()))
                        .imageUrl(user.getImageUrl())
                        .role(String.valueOf(user.getRole()))
                        .build()
        );
    }

    //    비밀번호 수정.
    @Transactional
    public ResponseDto<?> updatePassword(UserUpdateRequestDto requestDto,
                                         User user) {

        if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
            return ResponseDto.fail("PASSWORDS_NOT_MATCHED",
                    "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

//  변경 비밀번호를 입력하여 정상처리될 경우 비밀번호 업데이트 실행. 이후 password를 set을 이용하여 복호화 적용.
        user.update(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);

        return ResponseDto.success("비밀번호 수정이 완료되었습니다!");
    }

    //    이미지 수정
    @Transactional
    public ResponseDto<?> updateImage(User user, List<String> imgPaths, ImageUpdateRequestDto requestDto) {

//        이미지를 확인하고 user의 기존 이미지를 삭제. 이후 새로 넣은 이미지로 업데이트 되도록 설정.
        if (imgPaths != null) {
            String deleteImage = user.getImageUrl();
            awsS3UploadService.deleteFile(AwsS3UploadService.getFileNameFromURL(deleteImage));
        }
        user.update(requestDto);

        List<String> imgList = new ArrayList<>();
        for (String imgUrl : imgPaths) {
            Img img = new Img(imgUrl, user);
            imgList.add(img.getImageUrl());
        }
//        수정된 이미지를 imgList의 첫번째 배열에 저장한 후 user에 저장.
        user.imageSave(imgList.get(0));

        userRepository.save(user);

        return ResponseDto.success("프로필 사진 수정이 완료되었습니다!");
    }

    //  로그아웃. 토큰을 확인하여 일치할 경우 로그인 된 유저의 이미지와 토큰을 삭제.
    public ResponseDto<?> logout(HttpServletRequest request) {

        deleteToken(request);
        return ResponseDto.success("로그아웃이 완료되었습니다.");
    }


    @Transactional(readOnly = true)
    public User isPresentUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElse(null);
    }

    @Transactional(readOnly = true)
    public User isPresentNickname(String nickname) {
        Optional<User> optionalUser = userRepository.findByNickname(nickname);
        return optionalUser.orElse(null);
    }

    @Transactional
    public void deleteToken(HttpServletRequest request) {
        String accessToken = jwtUtil.resolveToken(request, JwtUtil.ACCESS_TOKEN);
        String refreshToken = jwtUtil.resolveToken(request, JwtUtil.REFRESH_TOKEN);
        Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
        long remainMilliSeconds = jwtUtil.getExpiration(accessToken);
        // 액세스 토큰 만료시점 까지 저장
        redisUtil.set("logout", accessToken, remainMilliSeconds);
        // refreshToken 삭제
        redisUtil.delete(info.getId());
    }

}
