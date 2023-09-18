package com.onpurple.controller;

import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MypageResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.MypageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/mypage/{userId}")
    public ApiResponseDto<MypageResponseDto> getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @PathVariable Long userId){

        return mypageService.getMyPage(userDetails.getUser(), userId);
    }

}
