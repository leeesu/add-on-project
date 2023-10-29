package com.onpurple.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onpurple.dto.request.LoginRequestDto;
import com.onpurple.dto.request.TokenDto;
import com.onpurple.dto.response.LoginResponseDto;
import com.onpurple.exception.CustomException;
import com.onpurple.enums.ErrorCode;
import com.onpurple.exception.ErrorResponse;
import com.onpurple.model.User;
import com.onpurple.redis.cacheRepository.UserCacheRepository;
import com.onpurple.repository.UserRepository;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static com.onpurple.enums.ErrorCode.LOGIN_FAIL_ERROR;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserCacheRepository userCacheRepository;


    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider, UserRepository userRepository, UserCacheRepository userCacheRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.userCacheRepository = userCacheRepository;
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
            throw new CustomException(ErrorCode.USER_INFO_NOT_MATCHED);
        }
    }

    @Override // 로그인 성공시 수행
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        User user = findUserOrCache(username);
        // 토큰 발급
        TokenDto tokenDto = jwtTokenProvider.reissueToken(username);
        // header 로 토큰 send
        jwtTokenProvider.tokenSetHeaders(tokenDto, response); // AccessToken header, RefreshToken cookie
        // 응답
        sendJsonResponse(response, user);
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

    private void sendJsonResponse(HttpServletResponse response, User user) throws IOException {
        LoginResponseDto responseData = LoginResponseDto.fromEntity(user);
        ResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, responseData);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");

        ErrorResponse errorResponse = new ErrorResponse(LOGIN_FAIL_ERROR, LOGIN_FAIL_ERROR.getMessage());
        ResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, errorResponse);
    }

}