package com.project.date.controller;

import com.project.date.dto.request.ProfileUpdateRequestDto;
import com.project.date.dto.response.ResponseDto;
import com.project.date.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/main")
    public ResponseDto<?> getAllProfiles() {

        return profileService.getAllProfiles();
    }

    @GetMapping( "/profile/{userId}")
    public ResponseDto<?> getProfile(@PathVariable Long userId) {

        return profileService.getProfile(userId);
    }

    @PatchMapping( "/mypage/userInfo")
    public ResponseDto<?> updateProfile(@RequestBody ProfileUpdateRequestDto requestDto, HttpServletRequest request) {

       return profileService.updateProfile(requestDto, request);
    }

}
