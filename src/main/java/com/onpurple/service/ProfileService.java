package com.project.date.service;

import com.project.date.dto.request.ProfileUpdateRequestDto;
import com.project.date.dto.response.ProfileResponseDto;
import com.project.date.dto.response.ResponseDto;
import com.project.date.jwt.TokenProvider;
import com.project.date.model.*;
import com.project.date.repository.ImgRepository;
import com.project.date.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProfileService {


    private final UserRepository userRepository;

    private final ImgRepository imgRepository;

    private final TokenProvider tokenProvider;


    //    전체 프로필 조회(메인페이지). user DB에 저장된 모든 내용을 찾은 후 리스트에 저장.
//    이후 저장한 내용을 반환.
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllProfiles() {
        List<User> profileList = userRepository.findAll();
        List<ProfileResponseDto> profileResponseDto = new ArrayList<>();
//        랜덤 추출 코드. 리스트를 불러올 때 기존의 경우 수정일자 순으로 정렬하였지만 무작위로 리스트를 불러올 때 사용.
        Collections.shuffle(profileList);
        for (User user : profileList) {
            List<Img> findImgList = imgRepository.findByUser_id(user.getId());
            List<String> imgList = new ArrayList<>();
            for (Img img : findImgList) {
                imgList.add(img.getImageUrl());
            }
            profileResponseDto.add(
                    ProfileResponseDto.builder()
                            .userId(user.getId())
                            .gender(user.getGender())
                            .nickname(user.getNickname())
                            .age(user.getAge())
                            .introduction(user.getIntroduction())
                            .imageUrl(user.getImageUrl())
                            .area(user.getArea())
                            .build()
            );
        }
        return ResponseDto.success(profileResponseDto);
    }

    //    상세 프로필 조회(디테일페이지). userId를 찾아 해당 id가 있을 경우 해당 내용을 조회.
    @Transactional
    public ResponseDto<?> getProfile(Long userId) {
        User user = isPresentProfile(userId);
        if (null == user) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 프로필입니다.");
        }

        List<Img> findImgList = imgRepository.findByUser_id(user.getId());
        List<String> imgList = new ArrayList<>();
        for (Img img : findImgList) {
            imgList.add(img.getImageUrl());
        }

        return ResponseDto.success(
                ProfileResponseDto.builder()
                        .userId(user.getId())
                        .imageUrl(user.getImageUrl())
                        .nickname(user.getNickname())
                        .age(user.getAge())
                        .mbti(user.getMbti())
                        .introduction(user.getIntroduction())
                        .idealType(user.getIdealType())
                        .job(user.getJob())
                        .hobby(user.getHobby())
                        .drink(user.getDrink())
                        .pet(user.getPet())
                        .smoke(user.getSmoke())
                        .likeMovieType(user.getLikeMovieType())
                        .area(user.getArea())

                        .build()
        );
    }

    //    프로필 수정. DB에 저장된 유저의 정보들 중 프로필에 해당되는 내용을 수정.
    @Transactional
    public ResponseDto<?> updateProfile(ProfileUpdateRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("USER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("USER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        User user = validateUser(request);
        if (null == user) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }


        user.update(requestDto);
        userRepository.save(user);
        return ResponseDto.success("프로필 정보 수정이 완료되었습니다!");
    }

    @Transactional
    public User validateUser(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getUserFromAuthentication();
    }

    @Transactional(readOnly = true)
    public User isPresentProfile(Long userId) {
        Optional<User> optionalProfile = userRepository.findById(userId);
        return optionalProfile.orElse(null);
    }
}