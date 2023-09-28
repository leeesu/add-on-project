package com.onpurple.service;


import com.onpurple.dto.request.ReCommentRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.ReCommentResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.enums.SuccessCode;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.Comment;
import com.onpurple.model.ReComment;
import com.onpurple.model.User;
import com.onpurple.repository.ReCommentRepository;
import com.onpurple.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.onpurple.enums.SuccessCode.*;

@Service
@RequiredArgsConstructor
public class ReCommentService {

    private final ValidationUtil validationUtil;
    private final ReCommentRepository reCommentRepository;

    /*
    * 대댓글 작성
    * @param commentId, reCommentRequestDto, user
    * @return ApiResponseDto<ReCommentResponseDto>
     */
    @Transactional
    public ApiResponseDto<ReCommentResponseDto> createReComment(Long commentId, ReCommentRequestDto requestDto, User user) {

        Comment comment = validationUtil.validateComment(commentId);

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

    /*
    * 대댓글 조회
    * @param commentId
    * @return ApiResponseDto<List<ReCommentResponseDto>>
     */

    @Transactional(readOnly = true)
    public ApiResponseDto<List<ReCommentResponseDto>> getAllReCommentsByComment(Long commentId) {


        Comment comment = validationUtil.validateComment(commentId);

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

    /*
    * 대댓글 수정
    * @param reCommentId, reCommentRequestDto, user
    * @return ApiResponseDto<ReCommentResponseDto>
     */
    @Transactional
    public ApiResponseDto<ReCommentResponseDto> updateReComment(Long reCommentId, ReCommentRequestDto requestDto, User user) {


        ReComment reComment = validationUtil.validateReComment(reCommentId);

        validateReCommentUser(reComment, user);

        reComment.update(requestDto);
        return ApiResponseDto.success(
                SUCCESS_RECOMMENT_EDIT.getMessage(),
                ReCommentResponseDto.fromEntity(reComment)
        );
    }

    /*
    * 대댓글 삭제
    * @param reCommentId, user
    * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> deleteReComment(Long reCommentId, User user) {

        ReComment reComment = validationUtil.validateReComment(reCommentId);

        validateReCommentUser(reComment, user);

        reCommentRepository.delete(reComment);
        return ApiResponseDto.success("delete success");
    }

    public void validateReCommentUser(ReComment reComment, User user) {
        if (reComment.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_USER_MATCH);
        }
    }


}