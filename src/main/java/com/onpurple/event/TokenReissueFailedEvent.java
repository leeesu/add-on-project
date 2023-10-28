package com.onpurple.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TokenReissueFailedEvent {
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public TokenReissueFailedEvent(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

}