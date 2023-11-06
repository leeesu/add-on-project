package com.onpurple.domain.user.controller;


import com.onpurple.domain.user.dto.ProfileUpdateRequestDto;
import com.onpurple.domain.user.dto.ProfileResponseDto;
import com.onpurple.domain.user.service.ProfileService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/main")
    public ApiResponseDto<List<ProfileResponseDto>> getAllProfiles() {

        return profileService.getAllProfiles();
    }

    @GetMapping( "/profile/{userId}")
    public ApiResponseDto<ProfileResponseDto> getProfile(@PathVariable final Long userId) {

        return profileService.getProfile(userId);
    }

    @PatchMapping( "/mypage/userInfo")
    public ApiResponseDto<MessageResponseDto> updateProfile(@RequestBody final ProfileUpdateRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

       return profileService.updateProfile(requestDto, userDetails.getUser());
    }

}
