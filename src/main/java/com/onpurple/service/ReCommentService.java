package com.onpurple.service;


import com.onpurple.dto.request.ReCommentRequestDto;
import com.onpurple.dto.response.ReCommentResponseDto;
import com.onpurple.dto.response.ResponseDto;
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

@Service
@RequiredArgsConstructor
public class ReCommentService {

    private final ValidationUtil validationUtil;
    private final ReCommentRepository reCommentRepository;

    @Transactional
    public ResponseDto<?> createReComment(Long commentId, ReCommentRequestDto requestDto, User user) {

        Comment comment = validationUtil.validateComment(commentId);

        ReComment reComment = ReComment.builder()
                .user(user)
                .comment(comment)
                .reComment(requestDto.getReComment())
                .build();

        reCommentRepository.save(reComment);
        return ResponseDto.success(
                ReCommentResponseDto.fromEntity(reComment)
        );
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllReCommentsByComment(Long commentId) {


        Comment comment = validationUtil.validateComment(commentId);

        List<ReComment> reCommentList = reCommentRepository.findAllByComment(comment);
        List<ReCommentResponseDto> reCommentResponseDto = new ArrayList<>();

        for (ReComment reComment : reCommentList) {
            reCommentResponseDto.add(
                    ReCommentResponseDto.fromEntity(reComment)
            );
        }

        return ResponseDto.success(reCommentResponseDto);

    }


    @Transactional
    public ResponseDto<?> updateReComment(Long reCommentId, ReCommentRequestDto requestDto, User user) {


        ReComment reComment = validationUtil.validateReComment(reCommentId);

        validateReCommentUser(reComment, user);

        reComment.update(requestDto);
        return ResponseDto.success(
                ReCommentResponseDto.fromEntity(reComment)
        );
    }

    @Transactional
    public ResponseDto<?> deleteReComment(Long reCommentId, User user) {

        ReComment reComment = validationUtil.validateReComment(reCommentId);

        validateReCommentUser(reComment, user);

        reCommentRepository.delete(reComment);
        return ResponseDto.success("delete success");
    }

    public void validateReCommentUser(ReComment reComment, User user) {
        if (reComment.validateUser(user)) {
            throw new CustomException(ErrorCode.INVALID_USER_MATCH);
        }
    }


}