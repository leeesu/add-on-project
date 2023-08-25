package com.onpurple.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.onpurple.dto.request.*;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.service.KakaoService;
import com.onpurple.service.UserService;
import com.onpurple.util.AwsS3UploadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final AwsS3UploadService s3Service;

    private final KakaoService kakaoService;

    @RequestMapping(value = "/user/signup", method = RequestMethod.POST)
    public ResponseDto<?> signup(@RequestPart(value = "info", required = false) @Valid SignupRequestDto requestDto,
                                 @RequestPart(value = "userInfo", required = false) UserInfoRequestDto userInfoRequestDto,
                                 @RequestPart(value = "imageUrl", required = false) List<MultipartFile> multipartFiles, HttpServletResponse response) {
        if (multipartFiles == null) {
            throw new NullPointerException("사진을 업로드해주세요");
        }
        List<String> imgPaths = s3Service.upload(multipartFiles);

        return userService.createUser(requestDto, userInfoRequestDto, imgPaths, response);
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public ResponseDto<?> login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse response) {

        return userService.login(requestDto, response);
    }

    @PostMapping("/user/idCheck/{username}")
    public ResponseDto<?> checkUser(@PathVariable String username) {

        return userService.checkUser(username);
    }

    @PostMapping("/user/nicknameCheck/{nickname}")
    public ResponseDto<?> checkNickname(@PathVariable String nickname) {

        return userService.checkNickname(nickname);
    }

    @RequestMapping(value = "/mypage/password", method = RequestMethod.PUT)
    public ResponseDto<?> passwordUpdate(@RequestBody UserUpdateRequestDto requestDto, HttpServletRequest request) {

        return userService.updatePassword(requestDto, request);
    }

    @RequestMapping(value = "/mypage/image", method = RequestMethod.PUT)
    public ResponseDto<?> imageUpdate(ImageUpdateRequestDto requestDto, @RequestPart("imageUrl") List<MultipartFile> multipartFiles, HttpServletRequest request) {

        if (multipartFiles == null) {
            throw new NullPointerException("사진을 업로드해주세요");
        }

        List<String> imgPaths = s3Service.upload(multipartFiles);
        return userService.updateImage(request, imgPaths, requestDto);
    }


    @RequestMapping(value = "/user/me", method = RequestMethod.GET)
    public ResponseDto<?> getUser(HttpServletRequest request) {

        return userService.getUser(request);
    }

    @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    public ResponseDto<?> logout(HttpServletRequest request) {

        return userService.logout(request);
    }

    @RequestMapping(value = "/user/kakaoLogin", method = RequestMethod.GET)
    public KakaoUserRequestDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        log.info(code);
        return kakaoService.kakaoLogin(code, response);
    }


}

