package com.onpurple.enums;

import com.amazonaws.AmazonServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SuccessCode {

    // 일반적인 성공 메시지
    SUCCESS("성공적으로 완료되었습니다."),

    // 관리자 관련 메시지
    ADMIN_SIGNUP("관리자 가입이 완료되었습니다."),
    ADMIN_COMMENT_DELETE("관리자 권한으로 댓글이 성공적으로 삭제되었습니다."),
    ADMIN_POST_DELETE("관리자에 권한으로 게시글이 성공적으로 삭제되었습니다."),

    // 로그인 및 회원가입 관련 메시지
    LOGIN("로그인이 성공적으로 완료되었습니다."),
    SIGNUP("회원가입이 성공적으로 완료되었습니다."),

    // 게시글 관련 메시지
    POST_REGISTER("게시글이 성공적으로 등록되었습니다."),
    POST_GET_DETAIL("게시글 조회가 성공적으로 완료되었습니다."),
    POST_GET_ALL("게시글 전체 조회가 성공적으로 완료되었습니다."),
    POST_EDIT("게시글이 성공적으로 수정되었습니다."),
    POST_DELETE("게시글이 성공적으로 삭제되었습니다."),

    // 댓글 관련 메시지
    COMMENT_REGISTER("댓글이 성공적으로 등록되었습니다."),
    COMMENT_GET_DETAIL("댓글 조회가 성공적으로 완료되었습니다."),
    COMMENT_GET_ALL("댓글 전체 조회가 성공적으로 완료되었습니다."),
    COMMENT_EDIT("댓글이 성공적으로 수정되었습니다."),
    COMMENT_DELETE("댓글이 성공적으로 삭제되었습니다.");
    private final String message;
}
