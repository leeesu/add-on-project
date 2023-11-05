package com.onpurple.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.onpurple.domain.user.dto.SignupRequestDto;
import com.onpurple.domain.user.dto.UserInfoRequestDto;
import com.onpurple.domain.user.dto.UserUpdateRequestDto;
import com.onpurple.domain.user.dto.UserResponseDto;
import com.onpurple.domain.user.service.UserService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.external.AwsS3UploadService;
import com.onpurple.global.kakao.dto.KakaoUserRequestDto;
import com.onpurple.global.kakao.service.KakaoService;
import com.onpurple.global.security.UserDetailsImpl;
import com.onpurple.global.helper.EntityValidatorManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

    @RequestMapping(value = "/user/signup", method = RequestMethod.POST)
    public ApiResponseDto<UserResponseDto> signup(@RequestPart(value = "info", required = false) @Valid final SignupRequestDto requestDto,
                                                  @RequestPart(value = "userInfo", required = false) final UserInfoRequestDto userInfoRequestDto,
                                                  @NotNull @RequestPart(value = "imageUrl", required = false) final MultipartFile multipartFiles) {
        String imgPaths = s3Service.uploadOne(multipartFiles);

        return userService.createUser(requestDto, userInfoRequestDto, imgPaths);
    }

    // login Filter단에서 이루어지게 구현
    @PostMapping("/user/idCheck/{username}")
    public ApiResponseDto<MessageResponseDto> checkUser(@PathVariable final String username) {

        return userService.checkUser(username);
    }

    @PostMapping("/user/nicknameCheck/{nickname}")
    public ApiResponseDto<MessageResponseDto> checkNickname(@PathVariable final String nickname) {

        return userService.checkNickname(nickname);
    }

    @RequestMapping(value = "/mypage/password", method = RequestMethod.PUT)
    public ApiResponseDto<MessageResponseDto> passwordUpdate(@RequestBody final UserUpdateRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return userService.updatePassword(requestDto, userDetails.getUser());
    }

    @RequestMapping(value = "/mypage/image", method = RequestMethod.PUT)
    public ApiResponseDto<MessageResponseDto> imageUpdate(@NotNull@RequestPart("imageUrl") final MultipartFile multipartFiles,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        String imgPaths = s3Service.uploadOne(multipartFiles);
        return userService.updateImage(userDetails.getUser(), imgPaths);
    }


    @RequestMapping(value = "/user/me", method = RequestMethod.GET)
    public ApiResponseDto<UserResponseDto> getUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return userService.getUser(userDetails.getUser());
    }

    @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    public ApiResponseDto<MessageResponseDto> logout(HttpServletRequest request, HttpServletResponse response) {

        return userService.logout(request,response);
    }

    @RequestMapping(value = "/user/kakaoLogin", method = RequestMethod.GET)
    public KakaoUserRequestDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        log.info(code);
        return kakaoService.kakaoLogin(code, response);
    }


}

