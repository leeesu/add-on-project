package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Default & Admin
    REQUEST_FAILED_ERROR("요청에 실패했습니다."),
    NOT_ADMIN_ERROR("관리자 권한이 없습니다."),


    // USER
    TOKEN_NOT_MATCHED("유효하지 않은 토큰입니다."),
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다."),
    INVALID_REQUEST("올바르지 않은 요청입니다."),
    USER_INFO_NOT_MATCHED("사용자 정보가 일치하지 않습니다."),
    PASSWORD_NOT_MATCHED("비밀번호가 일치하지 않습니다."),
    DUPLICATED_USER("중복된 사용자입니다."),
    TOKEN_NOT_FOUND("토큰을 찾을 수 없습니다."),
    TOKEN_ERROR("유효하지 않은 토큰입니다."),
    ACCESS_DENIED("로그인이 필요합니다."),
    ADMIN_PASSWORD_NOT_MATCHED("관리자 비밀번호가 일치하지 않습니다."),
    PASSWORD_CONFIRM_NOT_MATCHED("비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    DUPLICATED_USERNAME("중복된 사용자 이름입니다."),
    REFRESH_TOKEN_NOT_MATCHED("유효하지 않은 리프레시 토큰입니다."),
    REDIS_REFRESH_TOKEN_NOT_FOUND("Redis에서 RefreshToken을 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND("리프레시 토큰을 찾을 수 없습니다."),
    PROFILE_NOT_FOUND("프로필을 찾을 수 없습니다."),
    TOKEN_REISSUE_SCHEDULING_FAILURE("토큰 재발급 스케줄링에 실패했습니다."),

    // Image
    IMAGE_NOT_FOUND("이미지를 찾을 수 없습니다."),
    INVALID_IMAGE_FORMAT("지원되지 않는 이미지 형식입니다."),
    IMAGE_CONVERT_FAILD("이미지 변환 중 오류가 발생했습니다."),
    INVALID_IMAGE_TYPE("올바르지 않은 이미지 확장자입니다."),

    // Post
    INVALID_USER_MATCH("작성자만 수정할 수 있습니다."),
    POST_NOT_FOUND("게시글을 찾을 수 없습니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    POST_CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다."),


    // Comment & ReComment
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다."),
    NOT_FOUND_RECOMMENT("대댓글을 찾을 수 없습니다."),


    // Chat
    MATCHING_NOT_FOUND("회원과의 매칭 정보를 찾을 수 없습니다."),
    CHAT_ROOM_ALREADY_EXISTS("채팅방이 이미 존재합니다."),
    CHAT_ROOM_NOT_FOUND("채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_NOT_VALILD_MEMBER("채팅방 멤버가 아닙니다."),
    USER_NOT_PARTICIPANT("채팅방 참가자가 아닙니다."),

    // Like
    INVALID_SELF_LIKE("본인에게 좋아요를 할 수 없습니다."),
    DUPLICATE_LIKE_FAIL("중복으로 좋아요를 할 수 없습니다."),

    // Report
    INVALID_SELF_REPORT("신고 대상이 올바르지 않습니다."),
    REPORT_POST_NOT_FOUND("신고글을 찾을 수 없습니다."),


    UNSUPPORTED_ENCODING_ERROR("인코딩을 지원하지 않는 문자열입니다."),
    LIKE_ME_USER_NOT_FOUND("나를 좋아요한 회원이 없습니다."),
    POST_LOCKED_ERROR("현재 해당 리소스는 다른 트랜잭션에 의해 사용 중입니다. 나중에 다시 시도해 주세요");

    private final String message;
}
