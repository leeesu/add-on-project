package com.onpurple.service;


import com.onpurple.dto.request.ProfileUpdateRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.ProfileResponseDto;
import com.onpurple.model.User;
import com.onpurple.repository.UserRepository;
import com.onpurple.helper.EntityValidatorManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.onpurple.enums.SuccessCode.*;

@RequiredArgsConstructor
@Service
public class ProfileService {


    private final UserRepository userRepository;
    private final EntityValidatorManager entityValidatorManager;


    /*
    * 전체 프로필 조회
    * @return ApiResponseDto<List<ProfileResponseDto>>
    * 전체 프로필 조회(메인페이지). user DB에 저장된 모든 내용을 찾은 후 리스트에 저장,이후 저장한 내용을 반환.
     */

    @Transactional(readOnly = true)
    public ApiResponseDto<List<ProfileResponseDto>> getAllProfiles() {
        List<ProfileResponseDto> profileResponseDto = userRepository.findAll().stream()
                .map(ProfileResponseDto::allFromEntity)
                .collect(Collectors.toList());

        //  랜덤 추출 코드. 리스트를 불러올 때 기존의 경우 수정일자 순으로 정렬하였지만 무작위로 리스트를 불러올 때 사용
        Collections.shuffle(profileResponseDto);

        return ApiResponseDto.success(
                SUCCESS_PROFILE_GET_ALL.getMessage(),
                profileResponseDto);
    }

    /*
    * 상세 프로필 조회
    * @param userId
    * @return ApiResponseDto<ProfileResponseDto>
    * 상세 프로필 조회(디테일페이지). userId를 찾아 해당 id가 있을 경우 해당 내용을 조회.
     */
    @Transactional
    public ApiResponseDto<ProfileResponseDto> getProfile(Long userId) {
        User user = entityValidatorManager.validateProfile(userId);

        return ApiResponseDto.success(
                SUCCESS_PROFILE_GET_DETAIL.getMessage(),
                ProfileResponseDto.detailFromEntity(user)
        );
    }

    /*
    * 프로필 수정
    * @param requestDto, user
    * @return ApiResponseDto<MessageResponseDto>
    * 프로필 수정. DB에 저장된 유저의 정보들 중 프로필에 해당되는 내용을 수정.
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> updateProfile(ProfileUpdateRequestDto requestDto, User user) {

        user.update(requestDto);
        userRepository.save(user);
        return ApiResponseDto.success(
                SUCCESS_PROFILE_EDIT.getMessage());
    }
}