package com.onpurple.global.exception;

import com.onpurple.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private ErrorCode errorCode;
    private String detailMessage;

    public CustomException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = errorCode.getMessage();
    }
    // 기본메세지 대신에 디테일 메세지를 넣어주면 좀 더 다양한 정보를 표헌 가능
    public CustomException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }
}
