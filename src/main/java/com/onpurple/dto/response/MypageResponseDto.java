package com.onpurple.dto.response;

import com.onpurple.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.print.attribute.standard.MediaSize;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MypageResponseDto{

    private Long userId;

    private String nickname;

    private String imageUrl;

    private List<String> imgList;

    private Integer age;

    private String mbti;

    private String introduction;

    private String idealType;

    private String job;

    private String hobby;

    private String drink;

    private String pet;

    private String smoke;

    private String likeMovieType;

    private String area;

    private Integer likes;

    private Integer unLike;

    private List<LikedResponseDto> likedResponseDtoList;

    private List<OtherLikeResponseDto> otherLikeResponseDtoList;

    public static MypageResponseDto fromEntity(
            User user, List<LikedResponseDto> likedResponseDtoList,
            List<OtherLikeResponseDto> otherLikeResponseDtoList) {
        return MypageResponseDto.builder()
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
                .build();
    }

}