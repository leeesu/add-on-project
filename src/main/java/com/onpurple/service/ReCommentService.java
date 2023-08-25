package com.project.date.service;


import com.project.date.dto.request.ReCommentRequestDto;
import com.project.date.dto.response.PostResponseDto;
import com.project.date.dto.response.ReCommentResponseDto;
import com.project.date.dto.response.ResponseDto;
import com.project.date.jwt.TokenProvider;
import com.project.date.model.Comment;
import com.project.date.model.Post;
import com.project.date.model.ReComment;
import com.project.date.model.User;
import com.project.date.repository.CommentRepository;
import com.project.date.repository.ReCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReCommentService {

    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final ReCommentRepository reCommentRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> createReComment(Long commentId, ReCommentRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("USER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        User user = validateUser(request);
        if (null == user) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
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
    public ResponseDto<?> updateReComment(Long reCommentId, ReCommentRequestDto requestDto, HttpServletRequest request) {
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
    public ResponseDto<?> deleteReComment(Long reCommentId, HttpServletRequest request) {
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

    @Transactional
    public User validateUser(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getUserFromAuthentication();
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