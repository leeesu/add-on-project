package com.onpurple.service;


import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.enums.SuccessCode;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.*;
import com.onpurple.repository.CommentRepository;
import com.onpurple.repository.PostRepository;
import com.onpurple.helper.ImageUploaderManager;
import com.onpurple.helper.EntityValidatorManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j(topic = "관리자 기능")
public class AdminService {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final ImageUploaderManager imageUploaderManager;

    private final EntityValidatorManager entityValidatorManager;


    /**
     * 관리자 게시글 삭제
     * @param user
     * @param postId
     * @return ApiResponseDto<MessageResponseDto>
     * @throws CustomException
     * 토큰을 통해 해당 토큰의 정보를 확인. 이때 해당 정보의 Role 설정이 Admin일 경우  게시글 삭제가 가능하도록 설정.
     * 반대로 해당 정보의 Role 설정이 User인 경우 게시글 삭제가 진행되지 않도록 에러메시지 전송.
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> deletePostByAdmin(User user, Long postId) {


        Post post = entityValidatorManager.validatePost(postId);

        validationAdmin(user);
        postRepository.delete(post);
        List<String> imgList = imageUploaderManager.getListImage(post);

        imageUploaderManager.deleteImageList(post,imgList);

        return ApiResponseDto.success(
                SuccessCode.SUCCESS_ADMIN_COMMENT_DELETE.getMessage()
        );
    }

    /**
     * 관리자 댓글 삭제
     * @param user
     * @param commentId
     * @return ApiResponseDto<MessageResponseDto>
     * @throws CustomException
     */

    @Transactional
    public ApiResponseDto<MessageResponseDto> deleteCommentByAdmin(User user, Long commentId) {

        Comment comment = entityValidatorManager.validateComment(commentId);

        validationAdmin(user);
        commentRepository.delete(comment);

        return ApiResponseDto.success(SuccessCode.SUCCESS_ADMIN_COMMENT_DELETE.getMessage());
    }
    /**
     * 사용자가 'ADMIN' 역할인지 확인.
     *
     * @param user 'ADMIN' 역할인지 검증할 User 객체
     */
    @Transactional
    public void validationAdmin(User user) {
        if (!user.getRole().equals(Authority.ADMIN)) {
            log.error("{} 회원은 관리자가 아닙니다.", user);
            throw new CustomException(ErrorCode.NOT_ADMIN_ERROR);

        }
    }
}
