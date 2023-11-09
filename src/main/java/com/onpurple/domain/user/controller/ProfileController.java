package com.onpurple.domain.user.controller;


import com.onpurple.domain.user.dto.ProfileUpdateRequestDto;
import com.onpurple.domain.user.dto.ProfileResponseDto;
import com.onpurple.domain.user.service.ProfileService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "프로필 API", description = "프로필 조회, 수정")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/main")
    @Operation(summary = "메인페이지 조회", description = "메인페이지 조회")
    public ApiResponseDto<List<ProfileResponseDto>> getAllProfiles() {

        return profileService.getAllProfiles();
    }

    @GetMapping( "/profile/{userId}")
    @Operation(summary = "프로필 조회", description = "프로필 조회")
    @Parameter(name = "userId", description = "조회할 회원의 id", required = true)
    public ApiResponseDto<ProfileResponseDto> getProfile(@PathVariable final Long userId) {

        return profileService.getProfile(userId);
    }

    @PatchMapping( "/mypage/userInfo")
    public ApiResponseDto<MessageResponseDto> updateProfile(@RequestBody final ProfileUpdateRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {

       return profileService.updateProfile(requestDto, userDetails.getUser());
    }

}
