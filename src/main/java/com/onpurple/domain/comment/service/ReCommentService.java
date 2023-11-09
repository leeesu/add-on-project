package com.onpurple.domain.comment.service;


import com.onpurple.domain.comment.dto.ReCommentRequestDto;
import com.onpurple.domain.comment.dto.ReCommentResponseDto;
import com.onpurple.domain.comment.model.ReComment;
import com.onpurple.domain.comment.repository.ReCommentRepository;
import com.onpurple.domain.user.model.User;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.exception.CustomException;
import com.onpurple.global.enums.ErrorCode;
import com.onpurple.domain.comment.model.Comment;
import com.onpurple.global.helper.EntityValidatorManager;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.onpurple.global.enums.SuccessCode.*;

@Service
@RequiredArgsConstructor
public class ReCommentService {

    private final EntityValidatorManager entityValidatorManager;
    private final ReCommentRepository reCommentRepository;

    /**
     * 대댓글 작성
     * @param commentId
     * @param requestDto
     * @param user
     * @return ApiResponseDto<ReCommentResponseDto>
     */
    @Transactional
    public ApiResponseDto<ReCommentResponseDto> createReComment(Long commentId, ReCommentRequestDto requestDto, User user) {

        Comment comment = entityValidatorManager.validateComment(commentId);

        ReComment reComment = recommentFromRequest(requestDto, user, comment);

        reCommentRepository.save(reComment);
        return ApiResponseDto.success(
                SUCCESS_RECOMMENT_REGISTER.getMessage(),
                ReCommentResponseDto.fromEntity(reComment)
        );

    }
    private ReComment recommentFromRequest(ReCommentRequestDto reCommentRequestDto,
                                           User user, Comment comment) {
        return ReComment.builder()
                .user(user)
                .comment(comment)
                .reComment(reCommentRequestDto.getReComment())
                .build();
    }

    /**
     * 대댓글 조회
     * @param commentId
     * @return ApiResponseDto<List<ReCommentResponseDto>>
     */
    @Transactional(readOnly = true)
    public ApiResponseDto<List<ReCommentResponseDto>> getAllReCommentsByComment(Long commentId) {


        Comment comment = entityValidatorManager.validateComment(commentId);

        List<ReComment> reCommentList = reCommentRepository.findAllByComment(comment);
        List<ReCommentResponseDto> reCommentResponseDto = new ArrayList<>();

        for (ReComment reComment : reCommentList) {
            reCommentResponseDto.add(
                    ReCommentResponseDto.fromEntity(reComment)
            );
        }

        return ApiResponseDto.success(
                SUCCESS_RECOMMENT_GET_ALL.getMessage(),
                reCommentResponseDto);

    }

    /**
     * 대댓글 수정
     * @param reCommentId
     * @param requestDto
     * @param user
     * @return ApiResponseDto<ReCommentResponseDto>
     */
    @Transactional
    public ApiResponseDto<ReCommentResponseDto> updateReComment(Long reCommentId, ReCommentRequestDto requestDto, User user) {


        ReComment reComment = entityValidatorManager.validateReComment(reCommentId);

        validateReCommentUser(reComment, user);

        reComment.update(requestDto);
        return ApiResponseDto.success(
                SUCCESS_RECOMMENT_EDIT.getMessage(),
                ReCommentResponseDto.fromEntity(reComment)
        );
    }

    /**
     * 대댓글 삭제
     * @param reCommentId
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> deleteReComment(Long reCommentId, User user) {

        ReComment reComment = entityValidatorManager.validateReComment(reCommentId);

        validateReCommentUser(reComment, user);

        reCommentRepository.delete(reComment);
        return ApiResponseDto.success("delete success");
    }

    /**
     * 대댓글 작성자와 로그인한 사용자가 일치하는지 확인
     * @param reComment
     * @param user
     */
    public void validateReCommentUser(@NotNull ReComment reComment, User user) {
        if (reComment.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_USER_MATCH);
        }
    }


}