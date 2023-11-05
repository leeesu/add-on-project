package com.onpurple.global.security.event.handler;

import com.onpurple.domain.user.service.UserService;
import com.onpurple.global.security.event.TokenReissueFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenReissueFailedEventListener {

    private final UserService userService;
    @EventListener
    public void handleTokenReissueFailedEvent(TokenReissueFailedEvent event) {
        log.info("TokenReissueFailedEvent 발생, 로그아웃 처리");
        userService.logout(event.getRequest(), event.getResponse());
    }
}