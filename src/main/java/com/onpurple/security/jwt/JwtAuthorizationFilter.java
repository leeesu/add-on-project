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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.onpurple.security.jwt.JwtUtil.*;

@RequiredArgsConstructor
@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    @Value("${REFRESH_TOKEN_EXPIRE_TIME}")
    private long refreshTokenTime;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // 토큰 검증 AccessToken
        String accessToken = jwtUtil.resolveToken(req, ACCESS_TOKEN);
        String refreshToken = jwtUtil.resolveToken(req, REFRESH_TOKEN);
        if (StringUtils.hasText(accessToken)) {
            log.info(accessToken);
            if (!jwtUtil.validateToken(accessToken)) {
                log.error("Token Error");
                if(StringUtils.hasText(refreshToken)) {
                    boolean isRefreshToken = jwtUtil.validateRefreshToken(refreshToken);
                    if(isRefreshToken) {
                        Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
                        User user = userRepository.findById(Long.valueOf(info.getId())).orElseThrow(
                                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                        );
                        // 기존 refreshToken 토큰 삭제
                        redisUtil.delete(info.getId());
                        // 새로운 토큰 발급
                        TokenDto tokenDto = issueToken(user);

                        // header 로 토큰 send
                        jwtUtil.tokenAddHeaders(tokenDto, res);
                    }
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

    public TokenDto issueToken(User user) {
        TokenDto tokenDto = jwtUtil.createAllToken(
                jwtUtil.createAccessToken(user),
                jwtUtil.createRefreshToken(user)
        );
        // Redis에 저장
        redisUtil.set(
                String.valueOf(user.getId()),
                tokenDto.getRefreshToken(),
                refreshTokenTime // 만료 될 수 있도록 TTL 설정
        );
        return tokenDto;
    }

}