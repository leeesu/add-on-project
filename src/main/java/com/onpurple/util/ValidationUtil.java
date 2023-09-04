package com.onpurple.util;

import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.model.Comment;
import com.onpurple.model.Post;
import com.onpurple.model.ReComment;
import com.onpurple.model.User;
import com.onpurple.repository.CommentRepository;
import com.onpurple.repository.PostRepository;
import com.onpurple.repository.ReCommentRepository;
import com.onpurple.repository.UserRepository;
import com.onpurple.repository.post.PostCustomRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class ValidationUtil {

    public ValidationUtil(PostRepository postRepository,
                          CommentRepository commentRepository,
                          UserRepository userRepository,
                          ReCommentRepository reCommentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.reCommentRepository = reCommentRepository;
    }

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReCommentRepository reCommentRepository;

    @Transactional
    public Post assertValidatePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
        return post;
    }

    @Transactional(readOnly = true)
    public Comment assertValidateComment (Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()-> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );
        return comment;
    }

    @Transactional
    public User assertValidateProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.PROFILE_NOT_FOUND)
        );
        return user;
    }

    @Transactional
    public ReComment assertValidateReComment(Long reCommentId) {
        ReComment reComment = reCommentRepository.findById(reCommentId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_RECOMMENT)
        );
        return reComment;
    }
}
