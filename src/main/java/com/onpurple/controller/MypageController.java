package com.onpurple.controller;

import com.onpurple.dto.response.ResponseDto;
import com.onpurple.service.MypageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/mypage/{userId}")
    public ResponseDto<?> getMypage(HttpServletRequest request, @PathVariable Long userId){

        return mypageService.getMyPage(request, userId);
    }

}
