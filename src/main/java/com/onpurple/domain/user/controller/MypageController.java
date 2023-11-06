package com.onpurple.domain.user.controller;

import com.onpurple.domain.user.dto.MypageResponseDto;
import com.onpurple.domain.user.service.MypageService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
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
                                                       @PathVariable final Long userId){

        return mypageService.getMyPage(userDetails.getUser(), userId);
    }

}
