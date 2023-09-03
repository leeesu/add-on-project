package com.onpurple.service;

import com.onpurple.dto.response.ResponseDto;
import com.onpurple.dto.response.UnLikesResponseDto;
import com.onpurple.model.UnLike;
import com.onpurple.model.User;
import com.onpurple.repository.UnLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnLikeService {

    private final UnLikeRepository unLikeRepository;

    public UnLikeService(UnLikeRepository unLikeRepository) {

        this.unLikeRepository = unLikeRepository;
    }

    //    내가 싫어요 한 사람 리스트 조회.
//    264~295의 내가 좋아요한  사람 리스트 조회와 동일한 로직으로 구현.
    @Transactional(readOnly = true)
    public ResponseDto<?> getUnLike(User user) {
        List<UnLike> unLikesList = unLikeRepository.findAllByUser(user);

        List<UnLikesResponseDto> unLikesResponseDtoList = unLikesList
                .stream()
                .map(unLike -> UnLikesResponseDto.fromEntity(unLike))
                .collect(Collectors.toList());

        return ResponseDto.success(unLikesResponseDtoList);
    }
}
