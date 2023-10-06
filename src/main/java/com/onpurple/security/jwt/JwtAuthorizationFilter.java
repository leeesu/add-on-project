package com.onpurple.security.jwt;

import com.onpurple.dto.request.TokenDto;
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

import static com.onpurple.security.jwt.JwtTokenProvider.ACCESS_TOKEN;

@RequiredArgsConstructor
@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // 토큰 검증 AccessToken
        String accessToken = jwtTokenProvider.resolveToken(req, ACCESS_TOKEN);
        if (StringUtils.hasText(accessToken) && !(RedisUtil.checkValidateData(accessToken))) {
            if (!jwtTokenProvider.validateToken(accessToken)) {
                log.warn("[FAIL] AccessToken 검증 실패했습니다.");
                // 쿠키에서 토큰 추출후, 헤더로 토큰보내서 가져오기
                String refreshToken = jwtTokenProvider.refreshCookieRequest(req);
                TokenDto tokenDto = jwtTokenProvider.handleRefreshToken(refreshToken, req);
                accessToken = tokenDto.getAccessToken().substring(7);
                jwtTokenProvider.tokenSetHeaders(tokenDto, res);
                log.info("[SUCCESS] Access/Refresh 토큰 재발급에 성공했습니다.");
            }

            Claims info = jwtTokenProvider.getUserInfoFromToken(accessToken);

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

}
