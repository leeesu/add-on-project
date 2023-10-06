package com.onpurple.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.onpurple.dto.request.*;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.dto.response.UserResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.KakaoService;
import com.onpurple.service.UserService;
import com.onpurple.external.ValidationUtil;
import com.onpurple.external.s3.AwsS3UploadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final AwsS3UploadService s3Service;

    private final KakaoService kakaoService;
    private final ValidationUtil validationUtil;

    @RequestMapping(value = "/user/signup", method = RequestMethod.POST)
    public ApiResponseDto<UserResponseDto> signup(@RequestPart(value = "info", required = false) @Valid SignupRequestDto requestDto,
                                                  @RequestPart(value = "userInfo", required = false) UserInfoRequestDto userInfoRequestDto,
                                                  @RequestPart(value = "imageUrl", required = false) MultipartFile multipartFiles, HttpServletResponse response) {
        validationUtil.validateMultipartFile(multipartFiles);
        String imgPaths = s3Service.uploadOne(multipartFiles);

        return userService.createUser(requestDto, userInfoRequestDto, imgPaths);
    }

    // login Filter단에서 이루어지게 구현
    @PostMapping("/user/idCheck/{username}")
    public ApiResponseDto<?> checkUser(@PathVariable String username) {

        return userService.checkUser(username);
    }

    @PostMapping("/user/nicknameCheck/{nickname}")
    public ResponseDto<?> checkNickname(@PathVariable String nickname) {

        return userService.checkNickname(nickname);
    }

    @RequestMapping(value = "/mypage/password", method = RequestMethod.PUT)
    public ApiResponseDto<MessageResponseDto> passwordUpdate(@RequestBody UserUpdateRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return userService.updatePassword(requestDto, userDetails.getUser());
    }

    @RequestMapping(value = "/mypage/image", method = RequestMethod.PUT)
    public ApiResponseDto<MessageResponseDto> imageUpdate(@RequestPart("imageUrl") MultipartFile multipartFiles,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        validationUtil.validateMultipartFile(multipartFiles);

        String imgPaths = s3Service.uploadOne(multipartFiles);
        return userService.updateImage(userDetails.getUser(), imgPaths);
    }


    @RequestMapping(value = "/user/me", method = RequestMethod.GET)
    public ApiResponseDto<UserResponseDto> getUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return userService.getUser(userDetails.getUser());
    }

    @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    public ApiResponseDto<MessageResponseDto> logout(HttpServletRequest request) {

        return userService.logout(request);
    }

    @RequestMapping(value = "/user/kakaoLogin", method = RequestMethod.GET)
    public KakaoUserRequestDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        log.info(code);
        return kakaoService.kakaoLogin(code, response);
    }


}

