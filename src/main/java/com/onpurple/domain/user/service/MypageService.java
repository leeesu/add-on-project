package com.onpurple.domain.user.service;


import com.onpurple.domain.like.dto.LikedResponseDto;
import com.onpurple.domain.user.dto.OtherLikeResponseDto;

import com.onpurple.domain.like.model.Likes;
import com.onpurple.domain.like.repository.LikeRepository;
import com.onpurple.domain.user.dto.MypageResponseDto;
import com.onpurple.domain.user.model.User;
import com.onpurple.domain.user.repository.UserRepository;
import com.onpurple.global.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.onpurple.global.enums.SuccessCode.*;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;


    /**
     * 마이페이지 조회
     * @param user
     * @param userId
     * @return ApiResponseDto<MypageResponseDto>
     * 마이페이지 조회. 토큰을 확인하고 정보를 불러올 때 나를 좋아요 한 사람과 서로 좋아요 한 사람의 리스트를 불러온다.
     * 이때 기준이 되는 id를 내 userId로 설정.
     */
    @Transactional
    public ApiResponseDto<MypageResponseDto> getMyPage(User user, Long userId) {

    // 서로 좋아요 리스트 코드. likeRepository에서 서로 좋아요 한 id를 찾은 후 stream .distinct를 이용하여 중복제거.
    // 이후  매칭된 user를 list에 저장하고 이를 반환.
        List<OtherLikeResponseDto> otherLikeResponseDtoList = getOtherAndMeLikeList(userId);

    // 나를 좋아요 한 사람 리스트 코드. likeRepository에서 target이 된 userId(여기서의 userId의 경우 조회하는 사람을 의미)
    // 찾은 정보를 리스트에 저장한 후 반환.
        List<LikedResponseDto> likedResponseDtoList = getLikeMeList(userId);

        return ApiResponseDto.success(
                SUCCESS_MY_PAGE_GET.getMessage(),
                MypageResponseDto.fromEntity(
                user, likedResponseDtoList,otherLikeResponseDtoList));

    }


    /**
     * 서로 좋아요 리스트
     * @param userId
     * @return List<OtherLikeResponseDto>
     */
    private List<OtherLikeResponseDto> getOtherAndMeLikeList(Long userId) {
        List<Integer> likeList = likeRepository.likeToLikeUserId(userId)
                .stream()
                .distinct()
                .collect(Collectors.toList());

        return userRepository.matchingUser(likeList)
                .stream()
                .map(OtherLikeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }


    /**
     * 나를 좋아요 한 사람 리스트
     * @param userId
     * @return List<LikedResponseDto>
     */
    public List<LikedResponseDto> getLikeMeList(Long userId) {
        return likeRepository.findByTargetId(userId)
                .stream()
                .map(LikedResponseDto::fromEntity)
                .collect(Collectors.toList());
    }


}