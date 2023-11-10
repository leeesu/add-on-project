package com.onpurple.global.util;

import com.onpurple.global.enums.ErrorCode;
import com.onpurple.global.exception.CustomException;
import com.onpurple.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.onpurple.global.security.jwt.JwtTokenProvider.REFRESH_TOKEN;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtil {

    /**
     * RefreshToken을 쿠키에 저장
     * @param token
     * @param res
     */
    public static void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, StandardCharsets.UTF_8.toString());
            Cookie cookie = new Cookie(REFRESH_TOKEN, token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            JwtTokenProvider.logger.error("Unsupported Encoding Exception: ", e.getMessage());
            throw new CustomException(ErrorCode.UNSUPPORTED_ENCODING_ERROR);
        }
    }

    /**
     * RefreshToken을 쿠키에서 삭제
     * @param res
     */
    public static void deleteJwtFromCookie(HttpServletResponse res) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, null); // 쿠키의 값을 null로 설정
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0); // 쿠키의 만료 날짜를 과거로 설정
        res.addCookie(cookie);
    }
}
