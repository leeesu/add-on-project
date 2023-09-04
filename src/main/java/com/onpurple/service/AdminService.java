package com.onpurple.service;


import com.onpurple.dto.response.ResponseDto;
import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.model.*;
import com.onpurple.repository.CommentRepository;
import com.onpurple.repository.ImgRepository;
import com.onpurple.repository.PostRepository;
import com.onpurple.util.AwsS3UploadService;
import com.onpurple.util.ImageUtil;
import com.onpurple.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminService {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final ImageUtil imageUtil;

    private final ValidationUtil validationUtil;

//    관리자 권한으로 게시글 삭제.
//    토큰을 통해 해당 토큰의 정보를 확인. 이때 해당 정보의 Role 설정이 Admin일 경우  게시글 삭제가 가능하도록 설정.
//    반대로 해당 정보의 Role 설정이 User인 경우 게시글 삭제가 진행되지 않도록 에러메시지 전송.
    @Transactional
    public ResponseDto<?> deletePostByAdmin(User user, Long postId) {


        Post post = validationUtil.assertValidatePost(postId);

        validationAdmin(user);
        postRepository.delete(post);
        List<String> imgList = imageUtil.getListImage(post);

        imageUtil.deleteImageList(post,imgList);

        return ResponseDto.success(("관리자에 의해 성공적으로 삭제되었습니다."));
    }

//    관리자 권한으로 댓글 삭제.
//    관리자 권한으로 게시글 삭제와 동일한 로직으로 구현.
    @Transactional
    public ResponseDto<?> deleteCommentByAdmin(User user, Long commentId) {

        Comment comment = validationUtil.assertValidateComment(commentId);

        validationAdmin(user);
        commentRepository.delete(comment);

        return ResponseDto.success(("관리자에 의해 성공적으로 삭제되었습니다."));
    }

    @Transactional
    public void validationAdmin(User user) {
        if (!user.getRole().equals(Authority.ADMIN)) {
            log.error("{} 회원은 관리자가 아닙니다.", user);
            throw new CustomException(ErrorCode.NOT_ADMIN_ERROR);

        }
    }
}
