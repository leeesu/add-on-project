package com.onpurple.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onpurple.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.onpurple.enums.ErrorCode.ACCESS_DENIED;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(ACCESS_DENIED, ACCESS_DENIED.getMessage());
        ResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_FORBIDDEN, errorResponse);
    }
}
