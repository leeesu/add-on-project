package com.onpurple.security.jwt;

import com.onpurple.dto.request.TokenDto;
import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.model.User;
import com.onpurple.repository.UserRepository;
import com.onpurple.security.UserDetailsServiceImpl;
import com.onpurple.util.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.onpurple.enums.ExpireEnum.REFRESH_EXPIRE;
import static com.onpurple.security.jwt.JwtUtil.ACCESS_TOKEN;
import static com.onpurple.security.jwt.JwtUtil.REFRESH_TOKEN;

@RequiredArgsConstructor
@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // 토큰 검증 AccessToken
        String accessToken = jwtUtil.resolveToken(req, ACCESS_TOKEN);
        String refreshToken = jwtUtil.resolveToken(req, REFRESH_TOKEN);
        if (StringUtils.hasText(accessToken) && !(redisUtil.checkValidateToken(accessToken))) {
            if (!jwtUtil.validateToken(accessToken)) {
                log.info("[FAIL] AccessToken 검증 실패했습니다.");
                if(StringUtils.hasText(refreshToken)) {
                    log.info("[SUCCESS] RefreshToken이 존재합니다.");
                    String token = jwtUtil.validateRefreshToken(refreshToken);
                    log.info("[SUCCESS] RefreshToken 검증에 성공했습니다");
                    Claims info = jwtUtil.getUserInfoFromToken(token);
                    log.info("[SUCCESS] {} 회원의 토큰 재발급을 진행합니다", info.getSubject());
                    User user = userRepository.findByUsername(info.getSubject()).orElseThrow(
                            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                    );
                    // 기존 refreshToken 토큰 삭제
                    redisUtil.deleteToken(user.getUsername());
                    TokenDto tokenDto = reissueToken(user, res);
                    accessToken = tokenDto.getAccessToken().substring(7);
                    jwtUtil.tokenSetHeaders(tokenDto, res);
                }
            }

            Claims info = jwtUtil.getUserInfoFromToken(accessToken);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public void handlerRefreshTokenForAccessToken() {

    }

    public TokenDto reissueToken(User user, HttpServletResponse response) {
        TokenDto tokenDto = jwtUtil.createAllToken(jwtUtil.createAccessToken(user), jwtUtil.createRefreshToken(user));
        // redis로 RTK 저장
        redisUtil.saveToken(user.getUsername(), tokenDto.getRefreshToken(), REFRESH_EXPIRE.getTime());
        log.info("{} 회원의 토큰이 재발급 되었습니다.", user.getUsername());
        // header 로 토큰 send
        return tokenDto;
    }

}