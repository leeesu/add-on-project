package com.onpurple.domain.profile.controller;


import com.onpurple.domain.profile.dto.ProfileUpdateRequestDto;
import com.onpurple.domain.profile.dto.ProfileResponseDto;
import com.onpurple.domain.profile.service.ProfileService;
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
