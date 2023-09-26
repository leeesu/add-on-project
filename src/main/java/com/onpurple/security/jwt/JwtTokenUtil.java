package com.onpurple.security.jwt;

import com.onpurple.dto.request.TokenDto;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.Authority;
import com.onpurple.model.User;
import com.onpurple.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static com.onpurple.enums.ExpireEnum.*;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String REFRESH_TOKEN = "RefreshToken";
    private static final String AUTHORITIES_KEY = "auth";

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 로그");

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final UserRepository userRepository;
    private final JwtRefreshTokenUtil jwtRefreshTokenUtil;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기 Keys.hmacShaKeyFor(bytes);
    public String resolveToken(HttpServletRequest request, String tokenType) {
        String token = request.getHeader(tokenType);
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }else if(StringUtils.hasText(token) && tokenType.equals(REFRESH_TOKEN)){
            return token;
        }
        return null;
    }

    // AccessToken, RefreshToken을 생성하여 TokenDto에 담아서 반환
    public TokenDto createAllToken(String accessToken, String refreshToken) {
        TokenDto tokenDto = TokenDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return tokenDto;
    }
    // Token 생성
    public String createToken(User user, long expireTimeMillis, String tokenType) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expireTimeMillis);

        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .claim(AUTHORITIES_KEY, Authority.USER.toString())
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(key, signatureAlgorithm).compact();

        logger.info("발급된 {}의 만료시간은 {} 입니다", tokenType, expireTime);

        return token;
    }

    // AccessToken 생성
    public String createAccessToken(User user) {
        return BEARER_PREFIX + createToken(user, ACCESS_EXPIRE.getTime(), ACCESS_TOKEN);
    }



    //Token 검증
    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }



    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    // AccessToken HTTP header로, RefreshToken은 Cookie로 전달
    public void tokenSetHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.setHeader(ACCESS_TOKEN, tokenDto.getAccessToken());
        // 토큰 지워주기 -> 어차피 Redis에 새로운 토큰이 발급 되기 떄문에 굳이 헤더로 필요없는 토큰이 가는걸 막는 코드
        response.setHeader(REFRESH_TOKEN, null);
        addJwtToCookie(tokenDto.getRefreshToken(), response);
    }

    //JWT 토큰의 만료시간
    public Long getExpiration(String accessToken) {

        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }
    // RTR(Refresh Token Rotation) -> Access/RefreshToken 발급-재발급
    public TokenDto reissueToken(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                ()->new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        TokenDto tokenDto = createAllToken(createAccessToken(user),jwtRefreshTokenUtil.createRefreshToken(user));
        logger.info("{} 회원의 토큰이 발급 되었습니다.", user.getUsername());
        // header 로 토큰 send
        return tokenDto;
    }

    // JWT Cookie 에 저장
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, StandardCharsets.UTF_8.toString());
            Cookie cookie = new Cookie(REFRESH_TOKEN, token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error("Unsupported Encoding Exception: ", e.getMessage());
            throw new CustomException(ErrorCode.UNSUPPORTED_ENCODING_ERROR);
        }
    }
}