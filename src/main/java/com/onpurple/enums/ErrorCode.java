package com.onpurple.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // POST
    POST_NOT_FOUND("해당 게시글이 존재하지 않습니다"),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),

    // USER
    TOKEN_NOT_MATCHED("Token이 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR("서버에 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    USER_INFO_NOT_MATCHED("회원정보가 일치하지 않습니다"),
    PASSWORD_NOT_MATCHED("비밀번호가 일치하지 않습니다."),
    DUPLICATED_USER("중복된 사용자입니다."),
    TOKEN_NOT_FOUND("토큰을 찾을 수 없습니다."),
    TOKEN_ERROR("토큰이 올바르지 않습니다"),
    ACCESS_DENIED("로그인이 필요합니다."),
    ADMIN_PASSWORD_NOT_MATCHED("관리자 암호가 일치하지 않습니다."),
    IMAGE_NOT_FOUND("이미지를 등록해주세요"),
    INVALID_IMAGE_FORMAT("지원되지 않은 형식의 이미지파일 입니다."),
    IMAGE_CONVERT_FAILD("이미지 변환 중 에러가 발생헀습니다"),
    COMMENT_NOT_FOUND("해당 댓글이 존재하지 않습니다."),
    INVALID_USER_MATCH("작성자만 수정할 수 있습니다."),
    INVALID_SELF_LIKE("본인에게 좋아요 할 수 없습니다."),
    DUPLICATE_LIKE_FAIL("중복으로 좋아요 할  수 없습니다."),
    INVALID_SELF_REPORT("신고대상이 올바르지 않습니다."),
    REQUEST_FAILED_ERROR("요청에 실패했습니다."),
    INVALID_IMAGE_TYPE("올바르지 않은 이미지 확장자입니다."),
    NOT_ADMIN_ERROR("관리자가 아닙니다."),
    PROFILE_NOT_FOUND("프로필을 찾을 수 없습니다."),
    NOT_FOUND_RECOMMENT("해당 대댓글이 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_MATCHED("리프레쉬 토큰이 올바르지 않습니다."),
    REDIS_REFRESH_TOKEN_NOT_FOUND("레디스에서 리프레쉬 토큰을 찾을 수 없습니다."),
    POST_CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND("리프레시 토큰을 찾을 수 없습니다."),
    MATCHING_NOT_FOUND("해당 회원과 매칭정보를 찾을 수 없습니다"),
    CHAT_ROOM_ALREADY_EXISTS("채팅방이 이미 존재합니다."),
    CHAT_ROOM_NOT_FOUND("채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_NOT_VALILD_MEMBER("채팅방 멤버가 아닙니다."),
    USER_NOT_PARTICIPANT("채팅방의 참가자가 아닙니다."),
    DUPLICATED_USERNAME("중복된 사용자명 입니다"),
    PASSWORD_CONFIRM_NOT_MATCHED("비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    REPORT_POST_NOT_FOUND("신고글을 찾을 수 없습니다.");

    private final String message;
}
