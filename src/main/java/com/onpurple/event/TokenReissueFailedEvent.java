package com.onpurple.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TokenReissueFailedEvent {
    private final HttpServletRequest request;

    public TokenReissueFailedEvent(HttpServletRequest request) {
        this.request = request;
    }

}