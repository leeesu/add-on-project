package com.onpurple.service;


import com.onpurple.dto.request.ReCommentRequestDto;
import com.onpurple.dto.response.ReCommentResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.model.Comment;
import com.onpurple.model.ReComment;
import com.onpurple.model.User;
import com.onpurple.repository.CommentRepository;
import com.onpurple.repository.ReCommentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReCommentService {

    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;

    @Transactional
    public ResponseDto<?> createReComment(Long commentId, ReCommentRequestDto requestDto, User user) {

        Comment comment = isPresentComment(commentId);
        if (null == comment)
            return ResponseDto.fail("COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다.");


    ReComment reComment = ReComment.builder()
            .user(user)
            .comment(comment)
            .reComment(requestDto.getReComment())
            .build();

    reCommentRepository.save(reComment);
    return ResponseDto.success(
            ReCommentResponseDto.builder()
            .reCommentId(reComment.getId())
            .nickname(reComment.getUser().getNickname())
            .reComment(reComment.getReComment())
            .createdAt(reComment.getCreatedAt())
            .modifiedAt(reComment.getModifiedAt())
            .build()
        );
}

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllReCommentsByComment(Long commentId) {
        Comment comment = commentService.isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글입니다.");
        }

        List<ReComment> reCommentList = reCommentRepository.findAllByComment(comment);
        List<ReCommentResponseDto> reCommentResponseDto = new ArrayList<>();

        for (ReComment reComment : reCommentList) {
            reCommentResponseDto.add(
                    ReCommentResponseDto.builder()
                            .reCommentId(reComment.getId())
                            .nickname(reComment.getUser().getNickname())
                            .reComment((reComment.getReComment()))
                            .build()
            );
        }

        return ResponseDto.success(reCommentResponseDto);

    }


    @Transactional
    public ResponseDto<?> updateReComment(Long reCommentId, ReCommentRequestDto requestDto, User user) {


        ReComment reComment = isPresentReComment(reCommentId);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글입니다.");
        }

        if (reComment.validateUser(user)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        reComment.update(requestDto);
        return ResponseDto.success(
                ReCommentResponseDto.builder()
                        .reCommentId(reComment.getId())
                        .nickname(reComment.getUser().getNickname())
                        .reComment(reComment.getReComment())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> deleteReComment(Long reCommentId, User user) {

        ReComment reComment = isPresentReComment(reCommentId);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글입니다.");
        }

        if (reComment.validateUser(user)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
        }

        reCommentRepository.delete(reComment);
        return ResponseDto.success("delete success");
    }

    @Transactional(readOnly = true)
    public Comment isPresentComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        return optionalComment.orElse(null);
    }

    @Transactional(readOnly = true)
    public ReComment isPresentReComment(Long reCommentId) {
        Optional<ReComment> optionalReComment = reCommentRepository.findById(reCommentId);
        return optionalReComment.orElse(null);
    }
}