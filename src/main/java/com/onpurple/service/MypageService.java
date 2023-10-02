package com.onpurple.service;


import com.onpurple.dto.response.*;
import com.onpurple.enums.SuccessCode;
import com.onpurple.model.Likes;
import com.onpurple.model.User;
import com.onpurple.repository.LikeRepository;
import com.onpurple.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.onpurple.enums.SuccessCode.*;

@RequiredArgsConstructor
@Service
public class MypageService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;



    /*
    * 마이페이지 조회
    * @param user, userId
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

    // 서로 좋아요 리스트
    public List<OtherLikeResponseDto> getOtherAndMeLikeList(Long userId) {
        List<Integer> likeList = likeRepository.likeToLikeUserId(userId)
                .stream()
                .distinct()
                .collect(Collectors.toList());
        List<User> getLikeUser = userRepository.matchingUser(likeList);
        List<OtherLikeResponseDto> otherLikeResponseDtoList = new ArrayList<>();

        for (User list : getLikeUser) {
            otherLikeResponseDtoList.add(
                    OtherLikeResponseDto.fromEntity(list)
            );
        }
        return otherLikeResponseDtoList;
    }
    // 나를 좋아요한 리스트
    public List<LikedResponseDto> getLikeMeList(Long userId) {
        List<Likes> likeMeList = likeRepository.findByTargetId(userId);
        List<LikedResponseDto> likedResponseDtoList = new ArrayList<>();
        for (Likes list : likeMeList) {
            likedResponseDtoList.add(
                    LikedResponseDto.fromEntity(list)
            );
        }
        return likedResponseDtoList;
    }

}