package com.onpurple.service;


import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.enums.SuccessCode;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.*;
import com.onpurple.repository.CommentRepository;
import com.onpurple.repository.PostRepository;
import com.onpurple.util.ImageUtil;
import com.onpurple.util.ValidationUtil;
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

    private final ImageUtil imageUtil;

    private final ValidationUtil validationUtil;

//    관리자 권한으로 게시글 삭제.
//    토큰을 통해 해당 토큰의 정보를 확인. 이때 해당 정보의 Role 설정이 Admin일 경우  게시글 삭제가 가능하도록 설정.
//    반대로 해당 정보의 Role 설정이 User인 경우 게시글 삭제가 진행되지 않도록 에러메시지 전송.
    @Transactional
    public ApiResponseDto<MessageResponseDto> deletePostByAdmin(User user, Long postId) {


        Post post = validationUtil.validatePost(postId);

        validationAdmin(user);
        postRepository.delete(post);
        List<String> imgList = imageUtil.getListImage(post);

        imageUtil.deleteImageList(post,imgList);

        return ApiResponseDto.success(
                SuccessCode.ADMIN_COMMENT_DELETE.getMessage()
        );
    }

//    관리자 권한으로 댓글 삭제.
//    관리자 권한으로 게시글 삭제와 동일한 로직으로 구현.
    @Transactional
    public ApiResponseDto<MessageResponseDto> deleteCommentByAdmin(User user, Long commentId) {

        Comment comment = validationUtil.validateComment(commentId);

        validationAdmin(user);
        commentRepository.delete(comment);

        return ApiResponseDto.success(SuccessCode.ADMIN_COMMENT_DELETE.getMessage());
    }

    @Transactional
    public void validationAdmin(User user) {
        if (!user.getRole().equals(Authority.ADMIN)) {
            log.error("{} 회원은 관리자가 아닙니다.", user);
            throw new CustomException(ErrorCode.NOT_ADMIN_ERROR);

        }
    }
}
