package com.project.date.service;

import com.project.date.dto.response.ResponseDto;
import com.project.date.jwt.TokenProvider;
import com.project.date.model.*;
import com.project.date.repository.CommentRepository;
import com.project.date.repository.ImgRepository;
import com.project.date.repository.PostRepository;
import com.project.date.util.AwsS3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final TokenProvider tokenProvider;

    private final ImgRepository imgRepository;

    private final AwsS3UploadService awsS3UploadService;

//    관리자 권한으로 게시글 삭제.
//    토큰을 통해 해당 토큰의 정보를 확인. 이때 해당 정보의 Role 설정이 Admin일 경우  게시글 삭제가 가능하도록 설정.
//    반대로 해당 정보의 Role 설정이 User인 경우 게시글 삭제가 진행되지 않도록 에러메시지 전송.
    @Transactional
    public ResponseDto<?> deletePostByAdmin(HttpServletRequest request, Long postId) {
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
        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글입니다.");
        }

        if (!user.getRole().equals(Authority.ADMIN)) {
            return ResponseDto.fail("INVALID_ADMIN", "관리자가 아닙니다");
        }
        postRepository.delete(post);
        List<Img> findImgList = imgRepository.findByPost_Id(post.getId());
        List<String> imgList = new ArrayList<>();
        for (Img img : findImgList) {
            imgList.add(img.getImageUrl());
        }

        for (String imgUrl : imgList) {
            awsS3UploadService.deleteFile(AwsS3UploadService.getFileNameFromURL(imgUrl));
        }

        return ResponseDto.success(("관리자에 의해 성공적으로 삭제되었습니다."));
    }

//    관리자 권한으로 댓글 삭제.
//    관리자 권한으로 게시글 삭제와 동일한 로직으로 구현.
    @Transactional
    public ResponseDto<?> deleteCommentByAdmin(HttpServletRequest request, Long commentId) {
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
        Comment comment = isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글입니다.");
        }

        if (!user.getRole().equals(Authority.ADMIN)) {
            return ResponseDto.fail("INVALID_ADMIN", "관리자가 아닙니다");
        }
        commentRepository.delete(comment);

        return ResponseDto.success(("관리자에 의해 성공적으로 삭제되었습니다."));
    }

    @Transactional(readOnly = true)
    public Post isPresentPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
    }

    @Transactional(readOnly = true)
    public Comment isPresentComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        return optionalComment.orElse(null);
    }

    @Transactional
    public User validateUser(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getUserFromAuthentication();
    }
}
