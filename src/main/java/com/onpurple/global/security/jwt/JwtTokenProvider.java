package com.onpurple.global.security.jwt;

import com.onpurple.domain.user.model.User;
import com.onpurple.domain.user.repository.UserRepository;
import com.onpurple.global.redis.cacheRepository.UserCacheRepository;
import com.onpurple.global.redis.repository.TokenRepository;
import com.onpurple.global.role.Authority;
import com.onpurple.global.security.dto.TokenDto;
import com.onpurple.global.security.event.TokenReissueFailedEvent;
import com.onpurple.global.exception.CustomException;
import com.onpurple.global.enums.ErrorCode;
import com.onpurple.global.util.CookieUtil;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static com.onpurple.global.enums.ExpireEnum.*;
import static com.onpurple.global.enums.RedisKeyEnum.REFRESH_TOKEN_KEY;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String REFRESH_TOKEN = "RefreshToken";
    private static final String AUTHORITIES_KEY = "auth";

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JwtTokenProvider");

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TokenRepository tokenRepository;
    private final UserCacheRepository userCacheRepository;

    private static final String INVALID_JWT_SIGNATURE = "Invalid JWT signature. 유효하지 않는 JWT 서명 입니다.";
    private static final String EXPIRED_JWT_TOKEN = "Expired JWT token. 만료된 JWT token 입니다.";
    private static final String UNSUPPORTED_JWT_TOKEN = "Unsupported JWT token. 지원되지 않는 JWT 토큰 입니다.";
    private static final String JWT_CLAIMS_EMPTY = "JWT claims is empty. 잘못된 JWT 토큰 입니다.";

    // secretKey Base64 Decode
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * 토큰에서 회원 정보 추출 Keys.hmacShaKeyFor(bytes);
     * @param request
     * @param tokenType
     * @return
     */
    public String resolveToken(HttpServletRequest request, String tokenType) {
        String token = request.getHeader(tokenType);
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(7);
        }else if(StringUtils.hasText(token) && tokenType.equals(REFRESH_TOKEN)){
            return token;
        }
        return null;
    }

    /**
     * AccessToken, RefreshToken을 생성하여 TokenDto에 담아서 반환
     * @param accessToken
     * @param refreshToken
     * @return
     */
    public TokenDto createAllToken(String accessToken, String refreshToken) {
        TokenDto tokenDto = TokenDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return tokenDto;
    }
    /**
     * 토큰 생성, 토큰 타입을 확인해 AccessToken, RefreshToken을 생성
     * @param user
     * @param expireTimeMillis
     * @param tokenType
     * @return AccessToken, RefreshToken
     */
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

    /**
     * AccessToken 생성
     * @param user
     * @return AccessToken
     */
    public String createAccessToken(User user) {
        return BEARER_PREFIX + createToken(user, ACCESS_EXPIRE.getTime(), ACCESS_TOKEN);
    }

    /**
     * 토큰 검증, ATK, RTK의 유효성을 검증한다.
     * @param token
     * @return boolean
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error(INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            logger.error(EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            logger.error(UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            logger.error(JWT_CLAIMS_EMPTY);
        }
        return false;
    }


    /**
     * 토큰에서 회원 정보 추출
     * @param token
     * @return Claims
     */
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * 토큰을 HTTP Header와 쿠키로 보내준다. (AccessToken, RefreshToken)
     * @param tokenDto
     * @param response
     */
    public void tokenSetHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.setHeader(ACCESS_TOKEN, tokenDto.getAccessToken());
        CookieUtil.addJwtToCookie(tokenDto.getRefreshToken(), response);
    }

    /**
     * 토큰의 만료시간을 반환
     * @param token
     * @return
     */
    public Long getExpiration(String token) {

        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    /**
     * AccessToken, RefreshToken 재발급
     * @param username
     * @return TokenDto
     */
    public TokenDto reissueToken(String username) {
        User user = findUserOrCache(username);
        TokenDto tokenDto = createAllToken(createAccessToken(user),createRefreshToken(user));
        logger.info("{} 회원의 토큰이 발급 되었습니다.", user.getUsername());
        // header 로 토큰 send
        return tokenDto;
    }

    private User findUserOrCache(String username) {
        return userCacheRepository.getUser(username)
                .orElseGet(() -> {
                    User findDbUser = userRepository.findByUsername(username)
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                    userCacheRepository.saveUser(findDbUser);
                    return findDbUser;
                });
    }

    /**
     * RefreshToken 생성, Redis에 저장
     * @param user
     * @return
     */
    public String createRefreshToken(User user) {
        String refreshToken = createToken(user, REFRESH_EXPIRE.getTime(), REFRESH_TOKEN);

        tokenRepository.saveToken(REFRESH_TOKEN_KEY.getDesc()+user.getUsername(), refreshToken,
                REFRESH_EXPIRE.getTime());

        logger.info("Redis에 RefreshToken이 저장되었습니다.");
        logger.info("{} : Redis에 저장된 토큰 확인", tokenRepository.getToken(REFRESH_TOKEN_KEY.getDesc()+user.getUsername()));

        return refreshToken;
    }

    /**
     * 쿠키에서 RefreshToken 가져오기
     * @param request
     * @return RefreshToken
     */
    public String refreshCookieRequest(HttpServletRequest request){

        return getRefreshTokenFromRequest(request);
    }


    /**
     * RefreshToken 검증을 통한 AccessToken 재발급, 검증 실패시 로그아웃 이벤트 발행
     * @param refreshToken
     * @param request
     * @param response
     * @return TokenDto
     */
    public TokenDto handleRefreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {

        try {
            // RefreshToken의 만료를 검증하고, redis에 저장된 토큰과 비교검증한다.
            String validRefreshTokn = checkAndValidateToken(refreshToken);
            // 해당 토큰에서 회원정보 가져오기
            Claims userInfo = getUserInfoFromToken(validRefreshTokn);
            // 회원정보를 통해 Access/RefreshToken 재발급
            return reissueToken(userInfo.getSubject());

        } catch (Exception e) {

            logger.error("회원 토큰 재발급에 실패했습니다.", e.getMessage());

            // 재발급 실패시 로그아웃을 위한 이벤트 발행
            eventPublisher.publishEvent(new TokenReissueFailedEvent(request, response));

            throw new CustomException(ErrorCode.REQUEST_FAILED_ERROR);
        }
    }

    //RefreshToken 검증
    //DB에 저장돼 있는 토큰과 비교

    /**
     * RefreshToken 검증, redis에 저장된 토큰과 비교
     * @param token
     * @return
     */
    public String validateRefreshToken(String token) {
        //1차 토큰 검증
        if (!validateToken(token)) throw new CustomException(ErrorCode.TOKEN_NOT_MATCHED);

        //DB에 저장한 토큰 비교
        Claims info = getUserInfoFromToken(token);
        String redisRefreshToken = tokenRepository.getToken(REFRESH_TOKEN_KEY.getDesc()+info.getSubject());
        return refreshRedisValidate(redisRefreshToken, token);

    }

    /**
     * Redis에 저장된 RefreshToken과 검증
     * @param redisRefreshToken
     * @param token
     * @return
     */
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

    /**
     * HttpServletRequest 에서 Cookie Value : JWT 가져오기
     * @param req
     * @return RefreshToken
     */
    private String getRefreshTokenFromRequest(HttpServletRequest req) {
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

    /**
     * RefreshToken의 존재 확인 및 유효성 검증
     * @param refreshToken
     * @return RefreshToken
     */
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
    /**
     * 로그아웃시 AccessToken BlackList저장
     * @param request
     */
    @Transactional
    public void logoutBlackListToken(HttpServletRequest request) {
        String accessToken = resolveToken(request, JwtTokenProvider.ACCESS_TOKEN);
        Claims info = getUserInfoFromToken(accessToken);
        // 엑세스 토큰 남은시간
        long remainMilliSeconds = getExpiration(accessToken);
        // 액세스 토큰 만료시점 까지 저장 key가 accessToken
        tokenRepository.saveToken(accessToken, accessToken, remainMilliSeconds);
        // refreshToken redis에서 삭제 key로 BlackList토큰과 구분한다.
        tokenRepository.deleteToken(REFRESH_TOKEN_KEY.getDesc()+info.getSubject());
    }


}