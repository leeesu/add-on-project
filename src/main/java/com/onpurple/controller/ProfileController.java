package com.onpurple.controller;


import com.onpurple.dto.request.ProfileUpdateRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.ProfileResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ApiResponseDto<ProfileResponseDto> getProfile(@PathVariable Long userId) {

        return profileService.getProfile(userId);
    }

    @PatchMapping( "/mypage/userInfo")
    public ApiResponseDto<MessageResponseDto> updateProfile(@RequestBody ProfileUpdateRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

       return profileService.updateProfile(requestDto, userDetails.getUser());
    }

}
