package com.onpurple.domain.unLike.service;

import com.onpurple.domain.unLike.dto.UnLikesResponseDto;
import com.onpurple.domain.unLike.model.UnLike;
import com.onpurple.domain.unLike.repository.UnLikeRepository;
import com.onpurple.domain.user.model.User;
import com.onpurple.global.config.aop.DistributedLock;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.enums.SuccessCode;
import com.onpurple.global.helper.EntityValidatorManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.onpurple.global.enums.SuccessCode.*;

@Service
@Slf4j(topic = "싫어요 기능")
@RequiredArgsConstructor
public class UnLikeService {
    private final UnLikeRepository unLikeRepository;
    private final EntityValidatorManager entityValidatorManager;

    /**
     * 싫어요 기능
     * @DistributedLock 분산 Lock
     * @param targetId
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     */
    @DistributedLock
    @Transactional
    public ApiResponseDto<MessageResponseDto> userUnLike(Long targetId,
                                                         User user) {
        // 회원 프로필이 존재하는지 유효성 체크
        User target = entityValidatorManager.validateProfile(targetId);
        //좋아요 한 적 있는지 체크
        UnLike unLiked = unLikeRepository.findByUserAndTargetId(user, targetId).orElse(null);

        if (unLiked == null) {
            UnLike userUnLike = UnLike.builder()
                    .user(user)
                    .target(target)
                    .build();
            unLikeRepository.save(userUnLike);
            target.increaseUserUnLike();
            return ApiResponseDto.success(SuccessCode.SUCCESS_UN_LIKE.getMessage());
        } else {
            unLikeRepository.delete(unLiked);
            target.cancelUserUnLike();
            return ApiResponseDto.success(SUCCESS_UN_LIKE_CANCEL.getMessage());
        }
    }
    /**
     * 내가 싫어요 한 사람 리스트 조회
     * @param user
     * @return ApiResponseDto<List<UnLikesResponseDto>>
     * 좋아요한 사람 리스트 조회와 동일한 로직
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
