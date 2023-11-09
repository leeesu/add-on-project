package com.onpurple.global.exception;

import com.onpurple.global.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private ErrorCode errorCode;
    private String errorMessage;
}
