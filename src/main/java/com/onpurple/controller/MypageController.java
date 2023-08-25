package com.project.date.controller;

import com.project.date.dto.response.ResponseDto;
import com.project.date.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/mypage/{userId}")
    public ResponseDto<?> getMypage(HttpServletRequest request, @PathVariable Long userId){

        return mypageService.getMyPage(request, userId);
    }

}
