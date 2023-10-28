package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SuccessCode {

    // 일반적인 성공 메시지
    SUCCESS("성공적으로 완료되었습니다."),

    // 관리자 관련 메시지
    SUCCESS_ADMIN_SIGNUP("관리자 가입이 완료되었습니다."),
    SUCCESS_ADMIN_COMMENT_DELETE("관리자 권한으로 댓글이 성공적으로 삭제되었습니다."),
    SUCCESS_ADMIN_POST_DELETE("관리자에 권한으로 게시글이 성공적으로 삭제되었습니다."),

    // 회원 관련 메시지
    SUCCESS_GET_USER("회원정보 조회가 성공적으로 완료되었습니다."),
    SUCCESS_PASSWORD_CHANGE("비밀번호가 성공적으로 변경되었습니다."),

    // 로그인 및 회원가입 관련 메시지
    SUCCESS_LOGIN("로그인이 성공적으로 완료되었습니다."),
    SUCCESS_SIGNUP("회원가입이 성공적으로 완료되었습니다."),

    // 게시글 관련 메시지
    SUCCESS_POST_REGISTER("게시글이 성공적으로 등록되었습니다."),
    SUCCESS_POST_GET_DETAIL("게시글 조회가 성공적으로 완료되었습니다."),
    SUCCESS_POST_GET_ALL_CATEGORY("게시글 카테고리별 전체 조회가 성공적으로 완료되었습니다."),
    SUCCESS_POST_EDIT("게시글이 성공적으로 수정되었습니다."),
    SUCCESS_POST_DELETE("게시글이 성공적으로 삭제되었습니다."),

    // 댓글 관련 메시지
    SUCCESS_COMMENT_REGISTER("댓글이 성공적으로 등록되었습니다."),
    SUCCESS_COMMENT_GET_DETAIL("댓글 조회가 성공적으로 완료되었습니다."),
    SUCCESS_COMMENT_GET_ALL("댓글 전체 조회가 성공적으로 완료되었습니다."),
    SUCCESS_COMMENT_EDIT("댓글이 성공적으로 수정되었습니다."),
    SUCCESS_COMMENT_DELETE("댓글이 성공적으로 삭제되었습니다."),


    /*
    * 좋아요 관련 메세지
    * Post 게시글 좋아요, 취소
    * Comment 댓글 좋아요, 취소
    * UserLike 유저 좋아요, 취소
    * UserUnLike 유저 싫어요, 취소
     */
    SUCCESS_POST_LIKE("게시글 좋아요가 성공적으로 완료되었습니다."),
    SUCCESS_COMMENT_LIKE("댓글 좋아요가 성공적으로 완료되었습니다."),
    SUCCESS_USER_LIKE("유저 좋아요가 성공적으로 완료되었습니다."),
    SUCCESS_POST_LIKE_CANCEL("게시글 좋아요 취소가 성공적으로 완료되었습니다."),
    SUCCESS_COMMENT_LIKE_CANCEL("댓글 좋아요 취소가 성공적으로 완료되었습니다."),
    SUCCESS_USER_LIKE_CANCEL("유저 좋아요 취소가 성공적으로 완료되었습니다."),

    SUCCESS_MATCHING_FOUND("매칭정보를 성공적으로 찾았습니다."),
    SUCCESS_LIKE_USER_FOUND("좋아요한 유저를 성공적으로 찾았습니다."),
    SUCCESS_UN_LIKE_USER_FOUND("싫어요한 유저를 성공적으로 찾았습니다."),
    SUCCESS_MY_PAGE_GET("마이페이지 조회가 성공적으로 완료되었습니다."),
    SUCCESS_PROFILE_IMG_UPDATE("프로필 사진 수정이 완료되었습니다!"),
    SUCCESS_PROFILE_GET_ALL("프로필 리스트 조회가 성공적으로 완료되었습니다."),
    SUCCESS_PROFILE_GET_DETAIL("프로필 상세조회에 성공했습니다."),
    SUCCESS_PROFILE_EDIT("프로필 수정에 성공했습니다."),

    SUCCESS_REPORT_REGISTER ("신고글이 성공적으로 등록되었습니다."),
    SUCCESS_REPORT_GET_DETAIL("신고글 조회가 성공적으로 완료되었습니다."),
    SUCCESS_REPORT_GET_ALL("신고글 전체 조회가 성공적으로 완료되었습니다."),
    SUCCESS_REPORT_DELETE("신고글이 성공적으로 삭제되었습니다."),
    SUCCESS_POST_GET_ALL_CATEGORY_SEARCH("게시글 카테고리별 검색이 성공적으로 완료되었습니다."),
    SUCCESS_UN_LIKE_CANCEL("유저 싫어요 취소가 성공적으로 완료되었습니다."),
    SUCCESS_UN_LIKE("유저 싫어요가 성공적으로 완료되었습니다"),

    //대댓글 관련 메세지
    SUCCESS_RECOMMENT_EDIT("대댓글 수정에 성공했습니다."),
    SUCCESS_RECOMMENT_DELETE("대댓글 삭제에 성공했습니다."),
    SUCCESS_RECOMMENT_REGISTER("대댓글 등록에 성공했습니다."),
    SUCCESS_RECOMMENT_GET_ALL("대댓글 전체 조회에 성공했습니다."),
    SUCCESS_NICKNAME_CHANGE("사용 가능한 닉네임 입니다."),
    SUCCESS_LOGOUT("로그아웃에 성공했습니다."),
    ;


    private final String message;
}
