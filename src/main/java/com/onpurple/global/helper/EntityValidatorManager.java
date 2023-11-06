package com.onpurple.global.helper;

import com.onpurple.domain.comment.model.ReComment;
import com.onpurple.domain.comment.repository.CommentRepository;
import com.onpurple.domain.post.model.Post;
import com.onpurple.domain.post.repository.PostRepository;
import com.onpurple.domain.comment.repository.ReCommentRepository;
import com.onpurple.domain.user.model.User;
import com.onpurple.domain.user.repository.UserRepository;
import com.onpurple.global.exception.CustomException;
import com.onpurple.global.enums.ErrorCode;
import com.onpurple.domain.comment.model.Comment;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityValidatorManager {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReCommentRepository reCommentRepository;

    // 게시글 정보가 없을 경우 에러 메시지 전송.
    public Post validatePost(@NotNull Long postId) {
        return validateOrThrow(postRepository, postId, ErrorCode.POST_NOT_FOUND);
    }

    // 댓글 정보가 없을 경우 에러 메시지 전송.
    public Comment validateComment (@NotNull Long commentId) {
        return validateOrThrow(commentRepository, commentId, ErrorCode.COMMENT_NOT_FOUND);
    }
    // 회원 정보가 없을 경우 에러 메시지 전송.
    public User validateProfile(@NotNull Long userId) {
        return validateOrThrow(userRepository, userId,ErrorCode.PROFILE_NOT_FOUND);
    }



    // 대댓글 정보가 없을 경우 에러 메시지 전송.
    public ReComment validateReComment(@NotNull Long reCommentId) {
        return validateOrThrow(reCommentRepository, reCommentId, ErrorCode.NOT_FOUND_RECOMMENT);
    }

    private <T> T validateOrThrow(JpaRepository<T, Long> repository,
                                  Long id,
                                  ErrorCode errorCode){
        return repository.findById(id).orElseThrow(() -> new CustomException(errorCode));
    }

}
