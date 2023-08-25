package com.project.date.service;

import com.project.date.dto.response.*;
import com.project.date.jwt.TokenProvider;
import com.project.date.model.*;
import com.project.date.repository.LikeRepository;
import com.project.date.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class MypageService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

//    마이페이지 조회. 토큰을 확인하고 정보를 불러올 때 나를 좋아요 한 사람과 서로 좋아요 한 사람의 리스트를 불러온다.
//    이때 기준이 되는 id를 내 userId로 설정.
    @Transactional
    public ResponseDto<?> getMyPage(HttpServletRequest request, Long userId) {
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

//        서로 좋아요 리스트 코드. likeRepository에서 서로 좋아요 한 id를 찾은 후 stream .distinct를 이용하여 중복제거.
//        이후  매칭된 user를 list에 저장하고 이를 반환.
        List<Integer> likeList = likeRepository.likeToLikeUserId(userId)
                .stream()
                .distinct()
                .collect(Collectors.toList());
        List<User> getLikeUser = userRepository.matchingUser(likeList);
        List<OtherLikeResponseDto> otherLikeResponseDtoList = new ArrayList<>();

        for (User list : getLikeUser) {
            otherLikeResponseDtoList.add(
                    OtherLikeResponseDto.builder()
                            .userId(list.getId())
                            .imageUrl(list.getImageUrl())
                            .build()
            );
        }

//        나를 좋아요 한 사람 리스트 코드. likeRepository에서 target이 된 userId(여기서의 userId의 경우 조회하는 사람을 의미)
//        찾은 정보를 리스트에 저장한 후 반환.
        List<Likes> likeMeList = likeRepository.findByTargetId(userId);
        List<LikedResponseDto> likedResponseDtoList = new ArrayList<>();
        for (Likes list : likeMeList) {
            likedResponseDtoList.add(
                    LikedResponseDto.builder()
                            .userId(list.getUser().getId())
                            .imageUrl(list.getUser().getImageUrl())
                            .build()
            );
        }

        return ResponseDto.success(
                MypageResponseDto.builder()
                        .userId(user.getId())
                        .imageUrl(user.getImageUrl())
                        .age(user.getAge())
                        .mbti(user.getMbti())
                        .introduction(user.getIntroduction())
                        .area(user.getArea())
                        .job(user.getJob())
                        .hobby(user.getHobby())
                        .drink(user.getDrink())
                        .idealType(user.getIdealType())
                        .likeMovieType(user.getLikeMovieType())
                        .pet(user.getPet())
                        .smoke(user.getSmoke())
                        .likedResponseDtoList(likedResponseDtoList)
                        .otherLikeResponseDtoList(otherLikeResponseDtoList)
                        .build());
    }


    @Transactional
    public User validateUser(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getUserFromAuthentication();
    }

}