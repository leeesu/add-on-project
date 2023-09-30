package com.onpurple.service;

import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.dto.response.UnLikesResponseDto;
import com.onpurple.enums.SuccessCode;
import com.onpurple.model.UnLike;
import com.onpurple.model.User;
import com.onpurple.repository.UnLikeRepository;
import com.onpurple.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.onpurple.enums.SuccessCode.*;

@Service
@Slf4j(topic = "싫어요 기능")
public class UnLikeService {
    private final UnLikeRepository unLikeRepository;
    private final ValidationUtil validationUtil;

    public UnLikeService(UnLikeRepository unLikeRepository,
                         ValidationUtil validationUtil) {

        this.unLikeRepository = unLikeRepository;
        this.validationUtil = validationUtil;
    }

    /*
    * 싫어요 기능
    * @param targetId, user
    * @return ApiResponseDto<MessageResponseDto>
     */
    public ApiResponseDto<MessageResponseDto> userUnLike(Long targetId,
                                                            User user) {
        // 회원 프로필이 존재하는지 유효성 체크
        User target = validationUtil.validateProfile(targetId);
        //좋아요 한 적 있는지 체크
        UnLike unLiked = unLikeRepository.findByUserAndTargetId(user, targetId).orElse(null);

        if (unLiked == null) {
            UnLike userUnLike = UnLike.builder()
                    .user(user)
                    .target(target)
                    .build();
            unLikeRepository.save(userUnLike);
            int addUnLike = unLikeRepository.countByTargetId(targetId);
            log.info("지금 싫어요 수 : "+addUnLike);
            target.addUnLike(addUnLike);
            return ApiResponseDto.success(SuccessCode.SUCCESS_UN_LIKE.getMessage());
        } else {
            unLikeRepository.delete(unLiked);
            int cancelUnLike = unLikeRepository.countByTargetId(targetId);
            log.info("지금 싫어요 수 : "+cancelUnLike);
            target.minusUnLike(cancelUnLike);
            return ApiResponseDto.success(SUCCESS_UN_LIKE_CANCEL.getMessage());
        }
    }

    /*
    * 내가 싫어요 한 사람 리스트 조회
    * @param user
    * @return ApiResponseDto<List<UnLikesResponseDto>>
    * 좋아요한  사람 리스트 조회와 동일한 로직으로 구현.
     */
    @Transactional(readOnly = true)
    public ApiResponseDto<List<UnLikesResponseDto>> getUnLike(User user) {
        List<UnLike> unLikesList = unLikeRepository.findAllByUser(user);

        List<UnLikesResponseDto> unLikesResponseDtoList = unLikesList
                .stream()
                .map(unLike -> UnLikesResponseDto.fromEntity(unLike))
                .collect(Collectors.toList());

        return ApiResponseDto.success(
                SUCCESS_UN_LIKE_USER_FOUND.getMessage(),
                unLikesResponseDtoList);
    }
}
