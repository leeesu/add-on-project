package com.onpurple.security.jwt;

import com.onpurple.dto.request.TokenDto;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.model.Authority;
import com.onpurple.model.User;
import com.onpurple.repository.UserRepository;
import com.onpurple.util.RedisUtil;
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

    private final RedisUtil redisUtil;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기 Keys.hmacShaKeyFor(bytes);
    public String resolveToken(HttpServletRequest request, String tokenType) {
        String token = request.getHeader(tokenType);
        log.info("{} 타입의 토큰을 가져옵니다.", tokenType);
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }else if(StringUtils.hasText(token) && tokenType.equals(REFRESH_TOKEN)){
            return token;
        }
        return null;
    }

    public TokenDto createAllToken(String accessToken, String refreshToken) {
        TokenDto tokenDto = TokenDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return tokenDto;
    }

    // ACCESS_TOKEN 생성
    public String createAccessToken(User user) {
        Date date = new Date();

        String accessToken = BEARER_PREFIX + Jwts
                .builder()
                .setSubject(user.getUsername())
                .claim(AUTHORITIES_KEY, Authority.USER.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(date.getTime() + ACCESS_EXPIRE.getTime()))
                .signWith(key, signatureAlgorithm).compact();
        log.info("발급된 Access Token의 만료시간은 {} 입니다", new Date(date.getTime() + ACCESS_EXPIRE.getTime()));

        return accessToken;
    }

    // REFRESH_TOKEN 생성
    public String createRefreshToken(User user) {
        Date date = new Date();

        String refreshToken = Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(date.getTime() + REFRESH_EXPIRE.getTime()))
                .signWith(key, signatureAlgorithm).compact();
        log.info("발급된 Refresh Token의 만료시간은 {} 입니다", new Date(date.getTime() + REFRESH_EXPIRE.getTime()));
        // redis로 RTK 저장
        log.info("{} redis로 저장될 토큰 확인", refreshToken);
        redisUtil.saveToken(user.getUsername(), refreshToken, REFRESH_EXPIRE.getTime());
        log.info("redis로 RefreshToken이 저장되었습니다.");
        log.info("{} : redis에 저장된 토큰 확인", redisUtil.getToken(user.getUsername()));

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
    public String validateRefreshToken(String token) {
        //1차 토큰 검증
        if (!validateToken(token)) throw new CustomException(ErrorCode.TOKEN_NOT_MATCHED);

        //DB에 저장한 토큰 비교
        Claims info = getUserInfoFromToken(token);
        String redisRefreshToken = redisUtil.getToken(info.getSubject());
        if(redisRefreshToken.isEmpty()) {
            log.error("[ERROR] Redis에 RefreshToken이 존재하지 않습니다.");
            throw new CustomException(ErrorCode.REDIS_REFRESH_TOKEN_NOT_FOUND);
        }

        if (redisRefreshToken.equals(token)) {
            log.info("[SUCCESS] RedisRefreshToken과 검증 성공");
            return token;
        } else {
            logger.error("[FAIL] RedisRefreshToken과 검증 실패");
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_MATCHED);
        }
    }


    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public void tokenSetHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.setHeader(ACCESS_TOKEN, tokenDto.getAccessToken());
        response.setHeader(REFRESH_TOKEN, tokenDto.getRefreshToken());
    }

    //JWT 토큰의 만료시간
    public Long getExpiration(String accessToken) {

        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    public TokenDto reissueToken(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                ()->new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        TokenDto tokenDto = createAllToken(createAccessToken(user),createRefreshToken(user));
        log.info("{} 회원의 토큰이 재발급 되었습니다.", user.getUsername());
        // header 로 토큰 send
        return tokenDto;
    }


}