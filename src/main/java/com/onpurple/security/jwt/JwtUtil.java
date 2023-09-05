package com.onpurple.security.jwt;

import com.onpurple.dto.request.TokenDto;
import com.onpurple.model.Authority;
import com.onpurple.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static com.onpurple.enums.ExpireEnum.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private static final String BEARER_PREFIX = "Bearer ";

    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String REFRESH_TOKEN = "RefreshToken";
    private static final String AUTHORITIES_KEY = "auth";

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 로그");

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;

    private final RedisTemplate<String, String> redisTemplate;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

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
        }
        return null;
    }

    public TokenDto createAllToken(String accessToken, String refreshToken) {
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return tokenDto;
    }

    // ACCESS_TOKEN 생성
    public String createAccessToken(User user) {
        long now = (new Date().getTime());

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(user.getUsername())
                .claim(AUTHORITIES_KEY, Authority.USER.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(now + ACCESS_EXPIRE.getTime()))
                .signWith(key, signatureAlgorithm)
                .compact();
    }
    // REFRESH_TOKEN 생성
    public String createRefreshToken(User user) {
        long now = (new Date().getTime());

        String refreshToken = BEARER_PREFIX +
                Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(now + REFRESH_EXPIRE.getTime()))
                .signWith(key, signatureAlgorithm)
                .compact();

        return refreshToken;
    }



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


    //RefreshToken 검증
    //DB에 저장돼 있는 토큰과 비교
    public Boolean validateRefreshToken(String token) {
        //1차 토큰 검증
        if(!validateToken(token)) return false;

        //DB에 저장한 토큰 비교
        Claims claims = getUserInfoFromToken(token);
        String redisRefreshToken = String.valueOf(redisTemplate.opsForValue().get(claims.getId()));
        if(redisRefreshToken.equals(token)) return true;
        else return false;
    }



    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public void tokenAddHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader(ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(REFRESH_TOKEN, tokenDto.getRefreshToken());
    }

    //JWT 토큰의 만료시간
    public Long getExpiration(String accessToken){

        Date expiration = Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(accessToken).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }


}