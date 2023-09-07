package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SuccessCode {

    LOGIN_SUCCESS("로그인이 성공적으로 완료되었습니다."),
    SIGNUP_SUCCESS("회원가입이 성공적으로 완료되었습니다."),
    POST_REGISTER_SUCCESS("게시글이 성공적으로 등록되었습니다."),
    POST_LOOKUP_SUCCESS("게시글 조회가 성공적으로 완료되었습니다."),
    POST_ALL_LOOKUP_SUCCESS("게시글 전체 조회가 성공적으로 완료되었습니다."),
    POST_EDIT_SUCCESS("게시글이 성공적으로 수정되었습니다."),
    POST_DELETE_SUCCESS("게시글이 성공적으로 삭제되었습니다."),
    ADMIN_SIGNUP_SUCCESS("관리자 가입이 완료되었습니다")
    ;


    private final String message;
}
