package com.onpurple.domain.user.service;


import com.onpurple.domain.user.dto.ProfileUpdateRequestDto;
import com.onpurple.domain.user.dto.ProfileResponseDto;
import com.onpurple.domain.user.model.User;
import com.onpurple.domain.user.repository.UserRepository;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.enums.ErrorCode;
import com.onpurple.global.exception.CustomException;
import com.onpurple.global.helper.EntityValidatorManager;
import com.onpurple.global.redis.cacheRepository.CountCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.onpurple.global.enums.SuccessCode.*;

@RequiredArgsConstructor
@Service
@Slf4j(topic = "프로필")
public class ProfileService {


    private final UserRepository userRepository;
    private final EntityValidatorManager entityValidatorManager;
    private final CountCacheRepository countCacheRepository;
    private final Random random = new Random();
    private final int PAGE_SIZE = 10;

    /**
     * 전체 프로필 조회
     * @return ApiResponseDto<List<ProfileResponseDto>>
     * 전체 프로필 조회(메인페이지). user DB에 저장된 모든 내용을 찾은 후 리스트에 저장,이후 저장한 내용을 반환
     */
    @Transactional(readOnly = true)
    public ApiResponseDto<List<ProfileResponseDto>> getAllProfiles() {
        int pageSize = PAGE_SIZE; // 페이지당 표시할 사용자 수 설정

        // 총 사용자 수 조회
        long totalUsers = getTotalCount();

        // 무작위로 선택한 페이지 번호와 페이지 크기로 Pageable 객체 생성
        Pageable pageable = getRandomPageable(totalUsers, pageSize);
        // 해당 pageable로 모든 사용자를 조회하여 Page<User> 객체에 저장
        Page<User> usersPage = userRepository.findAll(pageable);

        List<ProfileResponseDto> profileResponseDtos = usersPage.stream()
                .map(ProfileResponseDto::allFromEntity)
                .collect(Collectors.toList());

        // 사용자 목록을 무작위 순서로 섞기
        Collections.shuffle(profileResponseDtos);

        return ApiResponseDto.success(
                SUCCESS_PROFILE_GET_ALL.getMessage(),
                profileResponseDtos);
    }

    // 무작위로 선택한 페이지 번호와 페이지 크기로 PageRequest 객체 생성
    private Pageable getRandomPageable(long totalUsers, int pageSize) {
        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        // 1부터 totalPages 사이의 무작위 페이지 번호 생성
        int randomPageNumber = random.nextInt(totalPages);
        return PageRequest.of(randomPageNumber, pageSize);
    }
    // 총 사용자 수 조회
    private long getTotalCount() {
        String currentCount = countCacheRepository.getCount().orElse("0");
        long totalUsers = Long.parseLong(currentCount);
        log.info("현재 총 사용자 수 : {}", totalUsers);
        // 사용자가 존재하지 않음.
        if (totalUsers == 0) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return totalUsers;
    }

    /**
     * 상세 프로필 조회
     * @param userId
     * @return ApiResponseDto<ProfileResponseDto>
     * 상세 프로필 조회(디테일페이지). userId를 찾아 해당 id가 있을 경우 해당 내용을 조회.
     */
    @Cacheable(value = "user", key = "#userId")
    @Transactional
    public ApiResponseDto<ProfileResponseDto> getProfile(Long userId) {
        User user = entityValidatorManager.validateProfile(userId);

        return ApiResponseDto.success(
                SUCCESS_PROFILE_GET_DETAIL.getMessage(),
                ProfileResponseDto.detailFromEntity(user)
        );
    }

    /**
     * 프로필 수정
     * @param requestDto
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     * 프로필 수정. DB에 저장된 유저의 정보들 중 프로필에 해당되는 내용을 수정.
     */
    @Transactional
    @CachePut(value = "user", key = "#user.id")
    public ApiResponseDto<MessageResponseDto> updateProfile(ProfileUpdateRequestDto requestDto, User user) {
        user.update(requestDto);
        return ApiResponseDto.success(
                SUCCESS_PROFILE_EDIT.getMessage());
    }
}