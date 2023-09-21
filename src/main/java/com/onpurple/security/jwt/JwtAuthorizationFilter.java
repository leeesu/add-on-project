package com.onpurple.security.jwt;

import com.onpurple.dto.request.TokenDto;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
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

import static com.onpurple.security.jwt.JwtUtil.ACCESS_TOKEN;
import static com.onpurple.security.jwt.JwtUtil.REFRESH_TOKEN;

@RequiredArgsConstructor
@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // 토큰 검증 AccessToken
        String accessToken = jwtUtil.resolveToken(req, ACCESS_TOKEN);
        if (StringUtils.hasText(accessToken) && !(redisUtil.checkValidateToken(accessToken))) {
            if (!jwtUtil.validateToken(accessToken)) {
                log.warn("[FAIL] AccessToken 검증 실패했습니다.");
                String refreshToken = jwtUtil.resolveToken(req, REFRESH_TOKEN);
                TokenDto tokenDto = handleRefreshToken(refreshToken);
                accessToken = tokenDto.getAccessToken().substring(7);
                jwtUtil.tokenSetHeaders(tokenDto, res);
            }

            Claims info = jwtUtil.getUserInfoFromToken(accessToken);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error("{} 인증 객체를 생성할 수 없습니다", e.getMessage());
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

    public TokenDto handleRefreshToken(String refreshToken) {

        try {
            if (StringUtils.hasText(refreshToken)) {
                log.info("[SUCCESS] RefreshToken이 존재합니다.");
                refreshToken = jwtUtil.validateRefreshToken(refreshToken);
                log.info("[SUCCESS] RefreshToken 검증에 성공했습니다");
                Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
                log.info("[SUCCESS] {} 회원의 토큰 재발급을 진행합니다", info.getSubject());
                TokenDto tokenDto = jwtUtil.reissueToken(info.getSubject());
                return tokenDto;
            }else {
                log.error("Refresh 토큰이 존재하지 않습니다.");
                throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("회원 토큰 재발급에 실패했습니다.", e.getMessage());
            throw new CustomException(ErrorCode.REQUEST_FAILED_ERROR);

        }
    }

}
