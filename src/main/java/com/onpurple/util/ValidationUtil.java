package com.onpurple.util;

import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.Comment;
import com.onpurple.model.Post;
import com.onpurple.model.ReComment;
import com.onpurple.model.User;
import com.onpurple.repository.CommentRepository;
import com.onpurple.repository.PostRepository;
import com.onpurple.repository.ReCommentRepository;
import com.onpurple.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationUtil {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReCommentRepository reCommentRepository;

    // 게시글 정보가 없을 경우 에러 메시지 전송.
    @Transactional
    public Post validatePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
        return post;
    }
    // 댓글 정보가 없을 경우 에러 메시지 전송.
    @Transactional(readOnly = true)
    public Comment validateComment (Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );
        return comment;
    }
    // 회원 정보가 없을 경우 에러 메시지 전송.
    @Transactional
    public User validateProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.PROFILE_NOT_FOUND)
        );
        return user;
    }
    // 대댓글 정보가 없을 경우 에러 메시지 전송.
    @Transactional
    public ReComment validateReComment(Long reCommentId) {
        ReComment reComment = reCommentRepository.findById(reCommentId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_RECOMMENT)
        );
        return reComment;
    }
    // 이미지 데이터가 없을 경우 에러 메시지 전송.
    @Transactional
    public void validateMultipartFile(MultipartFile multipartFiles) {
        if (multipartFiles == null) {
            throw new NullPointerException("사진을 업로드해주세요");
        }
    }
    // 이미지 데이터(리스트)가 없을 경우 에러 메시지 전송.
    @Transactional
    public void validateMultipartFiles(List<MultipartFile> multipartFiles) {

        if (multipartFiles == null) {
            throw new NullPointerException("사진을 업로드해주세요");
        }
    }
}
