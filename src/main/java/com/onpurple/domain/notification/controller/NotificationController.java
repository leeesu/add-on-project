package com.onpurple.domain.notification.controller;

import com.onpurple.domain.notification.dto.NotificationResponseDto;
import com.onpurple.domain.notification.service.NotificationService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "알림 관련 API", description = "알림 생성, 조회, 삭제")
@RestController
@RequiredArgsConstructor //생성자를 자동으로 생성
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "SSE 연결" ,
            description = "subscribe 엔드포인트로 들어오는 요청을 처리. " +
                    "produces 속성은 해당 메서드가 반환하는 데이터 형식을 지정")

    @GetMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId)  {

        return notificationService.subscribe(userDetails.getUser(), lastEventId);
    }

    @Operation(summary = "알림 전체 조회" , description = "알림 전체 조회")
    @GetMapping("/all")
    public ApiResponseDto<List<NotificationResponseDto>> getAllNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return notificationService.getAllNotifications(userDetails.getUser().getId());
    }

    @Operation(summary = "않읽은 알림" , description = "알림을 읽지 않은 알림 조회")
    @GetMapping("/unread")
    public ApiResponseDto<List<NotificationResponseDto>> getUnreadNotification(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return notificationService.getUnreadNotification(userDetails.getUser());
    }

    @Operation(summary = "알림 삭제" , description = "알림을 선택 삭제한다")
    @Parameter(name = "notificationId", description = "삭제할 알림의 id", required = true)
    @DeleteMapping("/{notificationId}")
    public ApiResponseDto<MessageResponseDto> deleteNotification(
            @PathVariable Long notificationId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails){

        return notificationService.deleteNotification(notificationId,userDetails.getUser());
    }

    @Operation(summary = "알림 전체 삭제" , description = "알림을 전체 삭제한다")
    @DeleteMapping("/allDelete")
    public ApiResponseDto<MessageResponseDto> allDeleteNotification(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return notificationService.allDeleteNotification(userDetails.getUser());
    }

    @Operation(summary = "알림 읽기" , description = "알림을 읽는다, 읽으면 읽음처리")
    @PostMapping("/read/{notificationId}")
    public ApiResponseDto<MessageResponseDto> readNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return notificationService.readNotification(notificationId, userDetails.getUser());
    }
}