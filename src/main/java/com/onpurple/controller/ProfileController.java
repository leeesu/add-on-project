package com.onpurple.controller;


import com.onpurple.dto.request.ProfileUpdateRequestDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
