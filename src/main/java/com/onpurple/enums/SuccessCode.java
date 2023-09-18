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

    // 회원 관련 메시지
    GET_USER("회원정보 조회가 성공적으로 완료되었습니다."),
    PASSWORD_CHANGE("비밀번호가 성공적으로 변경되었습니다."),

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
    COMMENT_DELETE("댓글이 성공적으로 삭제되었습니다."),


    /*
    * 좋아요 관련 메세지
    * Post 게시글 좋아요, 취소
    * Comment 댓글 좋아요, 취소
    * UserLike 유저 좋아요, 취소
    * UserUnLike 유저 싫어요, 취소
     */
    POST_LIKE("게시글 좋아요가 성공적으로 완료되었습니다."),
    COMMENT_LIKE("댓글 좋아요가 성공적으로 완료되었습니다."),
    USER_LIKE("유저 좋아요가 성공적으로 완료되었습니다."),
    POST_LIKE_CANCEL("게시글 좋아요 취소가 성공적으로 완료되었습니다."),
    COMMENT_LIKE_CANCEL("댓글 좋아요 취소가 성공적으로 완료되었습니다."),
    USER_LIKE_CANCEL("유저 좋아요 취소가 성공적으로 완료되었습니다."),

    MATCHING_FOUND("매칭정보를 성공적으로 찾았습니다."),
    LIKE_USER_FOUND("좋아요한 유저를 성공적으로 찾았습니다."),
    UN_LIKE_USER_FOUND("싫어요한 유저를 성공적으로 찾았습니다."),
    MY_PAGE_GET("마이페이지 조회가 성공적으로 완료되었습니다."),
    PROFILE_IMG_UPDATE("프로필 사진 수정이 완료되었습니다!"),
    PROFILE_GET_ALL("프로필 리스트 조회가 성공적으로 완료되었습니다."),
    PROFILE_GET_DETAIL("프로필 상세조회에 성공했습니다."),
    PROFILE_EDIT("프로필 수정에 성공했습니다."),
    RECOMMENT_EDIT("대댓글 수정에 성공했습니다."),
    RECOMMENT_DELETE("대댓글 삭제에 성공했습니다."),
    RECOMMENT_REGISTER("대댓글 등록에 성공했습니다."),
    RECOMMENT_GET_ALL("대댓글 전체 조회에 성공했습니다.");
    private final String message;
}
