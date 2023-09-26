package com.onpurple.security.jwt;

import com.onpurple.dto.request.TokenDto;
import com.onpurple.enums.ErrorCode;
import com.onpurple.exception.CustomException;
import com.onpurple.model.User;
import com.onpurple.service.UserService;
import com.onpurple.util.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static com.onpurple.enums.ExpireEnum.REFRESH_EXPIRE;
import static com.onpurple.enums.RedisKeyEnum.REFRESH_TOKEN_KEY;
import static com.onpurple.security.jwt.JwtTokenUtil.REFRESH_TOKEN;

@RequiredArgsConstructor
@Component
public class JwtRefreshTokenUtil {

    private final JwtTokenUtil jwtTokenUtil;
    private final RedisUtil redisUtil;
    private final UserService userService;

    public static final Logger logger = LoggerFactory.getLogger("JWT RefreshUtil 로그");
    // RefreshToken 생성
    public String createRefreshToken(User user) {
        String refreshToken = jwtTokenUtil.createToken(user, REFRESH_EXPIRE.getTime(), REFRESH_TOKEN);

        redisUtil.saveData(REFRESH_TOKEN_KEY.getDesc()+user.getUsername(), refreshToken,
                REFRESH_EXPIRE.getTime());

        logger.info("Redis에 RefreshToken이 저장되었습니다.");
        logger.info("{} : Redis에 저장된 토큰 확인", redisUtil.getData(REFRESH_TOKEN_KEY.getDesc())+user.getUsername());

        return refreshToken;
    }

    // httpOnly, Secure설정이 되어있는 쿠키 -> 자바스크립트에서 접근 불가
    // RefreshToken을 가져와서 헤더로 보낸다.
    public String refreshSetHeader(HttpServletRequest request, HttpServletResponse response) {

        response.setHeader(REFRESH_TOKEN, refreshCookieRequest(request));
        String refreshToken = jwtTokenUtil.resolveToken(request, REFRESH_TOKEN);
        return refreshToken;
    }
    // 쿠키에서 RefreshToken 가져오기
    private String refreshCookieRequest(HttpServletRequest request){
        return getRefreshTokenFromRequest(request);
    }


    // 리프레시 토큰 검증을 통한 AccessToken 재발급
    public TokenDto handleRefreshToken(String refreshToken, HttpServletRequest request) {

        try {
            // RefreshToken의 만료를 검증하고, redis에 저장된 토큰과 비교검증한다.
            String validRefreshTokn = checkAndValidateToken(refreshToken);
            // 해당 토큰에서 회원정보 가져오기
            Claims userInfo = jwtTokenUtil.getUserInfoFromToken(validRefreshTokn);
            // 회원정보를 통해 Access/RefreshToken 재발급
            return jwtTokenUtil.reissueToken(userInfo.getSubject());

        } catch (Exception e) {

            logger.error("회원 토큰 재발급에 실패했습니다.", e.getMessage());

            // 재발급 실패시 로그아웃
            userService.logout(request);
            throw new CustomException(ErrorCode.REQUEST_FAILED_ERROR);
        }
    }

    //RefreshToken 검증
    //DB에 저장돼 있는 토큰과 비교
    public String validateRefreshToken(String token) {
        //1차 토큰 검증
        if (!jwtTokenUtil.validateToken(token)) throw new CustomException(ErrorCode.TOKEN_NOT_MATCHED);

        //DB에 저장한 토큰 비교
        Claims info = jwtTokenUtil.getUserInfoFromToken(token);
        String redisRefreshToken = redisUtil.getData(REFRESH_TOKEN_KEY.getDesc()+info.getSubject());
        return refreshRedisValidate(redisRefreshToken, token);

    }

    private String refreshRedisValidate(String redisRefreshToken, String token) {

        if(redisRefreshToken.isEmpty()) {
            logger.error("[ERROR] Redis에 RefreshToken이 존재하지 않습니다.");
            throw new CustomException(ErrorCode.REDIS_REFRESH_TOKEN_NOT_FOUND);
        }

        if (redisRefreshToken.equals(token)) {
            logger.info("[SUCCESS] RedisRefreshToken과 검증 성공");
            return token;
        } else {
            logger.error("[FAIL] RedisRefreshToken과 검증 실패");
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_MATCHED);
        }
    }

    // HttpServletRequest 에서 Cookie Value : JWT 가져오기
    public String getRefreshTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(REFRESH_TOKEN)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.toString());
                    } catch (UnsupportedEncodingException e) {
                        logger.error("Unsupported Encoding Exception: ", e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    // 토큰의 존재 확인 및 유효성 검증
    private String checkAndValidateToken(String refreshToken) {
        if (StringUtils.hasText(refreshToken)) {
            logger.info("[SUCCESS] RefreshToken이 존재합니다.");
            refreshToken = validateRefreshToken(refreshToken);
            logger.info("[SUCCESS] RefreshToken 검증에 성공했습니다");
            return refreshToken;
        } else {
            logger.error("Refresh 토큰이 존재하지 않습니다.");
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }
}
