package com.onpurple.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onpurple.dto.request.LoginRequestDto;
import com.onpurple.dto.request.TokenDto;
import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.model.Authority;
import com.onpurple.model.User;
import com.onpurple.repository.UserRepository;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private final RedisUtil redisUtil;

    @Value("${REFRESH_TOKEN_EXPIRE_TIME}")
    private long refreshTokenTime;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil, UserRepository userRepository, RedisUtil redisUtil) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.redisUtil = redisUtil;
        // 로그인 처리를 여기서 처리한다.
        setFilterProcessesUrl("/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            // 해당 데이터를 어떤 객체로 맵핑할 건지 알려준다.
            // request로 넘어오는 해당 데이터를 가져와서 변환할 object 타입을 줌.
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            // 검증을하는 인증처리를 하는 메서드
            return getAuthenticationManager().authenticate(
                    // 토큰 인증 객체를 넣어준다. 권한은 null
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override // 로그인 성공시 수행
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        TokenDto tokenDto = jwtUtil.createAllToken(jwtUtil.createAccessToken(user), jwtUtil.createRefreshToken(user));
        // header 로 토큰 send
        jwtUtil.tokenAddHeaders(tokenDto, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        response.setStatus(401);
    }
}