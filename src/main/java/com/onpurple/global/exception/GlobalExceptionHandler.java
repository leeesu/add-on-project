package com.onpurple.global.exception;

import com.onpurple.global.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ErrorResponse handleException(
            CustomException e,
            HttpServletRequest request
    ) {
        log.error("errorCode: {}, url : {}, message: {}",
                e.getErrorCode(), request.getRequestURI(), e.getDetailMessage());
        return ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getDetailMessage())
                .build();
    }

    @ExceptionHandler(value = {
            HttpRequestMethodNotSupportedException.class,
            MethodArgumentNotValidException.class})
    public ErrorResponse handlerBadRequest(
            Exception e, HttpServletRequest request) {
        log.error("url : {}, message : {}", request.getRequestURI(), e.getMessage());
        return ErrorResponse.builder()
                .errorCode(ErrorCode.INVALID_REQUEST)
                .errorMessage(ErrorCode.INVALID_REQUEST.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(
            Exception e, HttpServletRequest request) {
        log.error("url : {}, message : {}", request.getRequestURI(), e.getMessage());
        return ErrorResponse.builder()
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                .errorMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .build();
    }
}