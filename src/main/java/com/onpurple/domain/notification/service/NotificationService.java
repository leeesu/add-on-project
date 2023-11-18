package com.onpurple.domain.notification.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.onpurple.domain.notification.dto.NotificationResponseDto;
import com.onpurple.domain.notification.model.Notification;
import com.onpurple.domain.notification.repository.EmitterRepository;
import com.onpurple.domain.notification.repository.NotificationRepository;
import com.onpurple.domain.notification.enums.NotificationType;
import com.onpurple.domain.user.model.User;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.enums.ErrorCode;
import com.onpurple.global.enums.SuccessCode;
import com.onpurple.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmitterRepository emitterRepository ;
    private final NotificationRepository notificationRepository;

    //DEFAULT_TIMEOUT을 기본값으로 설정
    private static final Long DEFAULT_TIMEOUT = 60 * 60 * 1000L;


    /**
     * SseEmitter 객체를 생성하고, 이를 EmitterRepository에 저장한다.
     * 사용자가 실시간 알림을 수신하기 위해 서버에 연결을 요청할 때 호출되는 메서드
     * @param user
     * @param lastEventId
     * @return
     */
    public SseEmitter subscribe(User user, String lastEventId) {
        String emitterId = makeTimeIncludeId(user.getId());
        // lastEventId가 있을 경우, userId와 비교해서 유실된 데이터일 경우 재전송할 수 있다.

        emitterRepository.deleteAllEmitterStartWithId(String.valueOf(user.getId()));

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> {
            log.info("SSE 연결 Complete");
            emitterRepository.deleteById(emitterId);
//            onClientDisconnect(emitter, "Compeletion");
        });
        //시간이 만료된 경우 자동으로 레포지토리에서 삭제하고 클라이언트에서 재요청을 보낸다.
        emitter.onTimeout(() -> {
            log.info("SSE 연결 Timeout");
            emitterRepository.deleteById(emitterId);
        });
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));
        //Dummy 데이터를 보내 503에러 방지. (SseEmitter 유효시간 동안 어느 데이터도 전송되지 않으면 503에러 발생)
        String eventId = makeTimeIncludeId(user.getId());
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + user.getId() + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방한다.
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, user.getId(), emitterId, emitter);
        }
        return emitter;
    }
    private String makeTimeIncludeId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }

    /**
     * 알림을 보내는 주요 역할을 하는 메서드, 실제전송은 sendNotification() 사용해 이루어짐
     * send메서드에서 createNotification() 메서드를 통해 Notification 객체를 생성하고,
     * 이를 NotificationRepository에 저장, re
     * @param receiver
     * @param notificationType
     * @param message
     * @param senderUsername
     * @param senderNickname
     * @param senderProfileImageUrl
     */
    public void send(User receiver, NotificationType notificationType,
                     String message, String senderUsername, String senderNickname,
                     String senderProfileImageUrl) {
        //send() 메서드는 Member 객체와 AlarmType 열거형, 알림 메시지(String)와 알림 상태(Boolean) 값을 인자로 받아 기능을 구현한다.
        Notification notification = notificationRepository.save(createNotification(receiver, notificationType, message, senderUsername, senderNickname, senderProfileImageUrl));

        // Notification 객체의 수신자 ID를 추출하고,
        String receiverId = String.valueOf(receiver.getId());
        // 현재 시간을 포함한 고유한 eventId를 생성한다.
        String eventId = receiverId + "_" + System.currentTimeMillis();

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationResponseDto.fromEntity(notification));
                }
        );
    }

    /**
     * 알림을 전송하는 역할. send 메소드에서 찾은 각 emitter(user)에게 Notification 객체를 전송
     * @param emitter
     * @param eventId
     * @param emitterId
     * @param data
     */
    public void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            log.info("eventId : " + eventId);
            log.info("data" + data);
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data));

        } catch (IOException exception) {
            log.info("예외 발생해서 emitter 삭제됨");
            //알림 전송 중 오류가 발생하면, 해당 emitter를 삭제
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, Long memberId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));

        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }
    // Notification 객체를 생성
    private Notification createNotification(User receiver, NotificationType notificationType,
                                            String message, String senderUsername, String senderNickname,
                                            String senderProfileImageUrl) {
        return Notification.builder()
                .receiver(receiver)
                .notificationType(notificationType)
                .message(message)
                .senderUsername(senderUsername)
                .senderNickname(senderNickname)
                .senderProfileImageUrl(senderProfileImageUrl)
                .build();
    }

    /**
     * 알림 전체 조회
     * @param userId
     * @return ApiResponseDto<List<NotificationResponseDto>>
     */
    public ApiResponseDto<List<NotificationResponseDto>> getAllNotifications(Long userId) {

        List<Notification> notifications = notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userId);
        log.info(userId + " 님이 알림 전체 조회를 했습니다.");
        return ApiResponseDto.success(SuccessCode.SUCCESS_NOTIFICATION_GET_ALL.getMessage(),
                notifications.stream()
                .map(NotificationResponseDto::fromEntity)
                .collect(Collectors.toList()));
    }

    /**
     * 선택 알림 삭제
     * @param notificationId
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     */
    // 선택된 알림 삭제
    @Transactional
    public ApiResponseDto<MessageResponseDto> deleteNotification(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new NotFoundException("Notification not found"));

        // 확인한 유저가 알림을 받은 대상자가 아니라면 예외 발생
        if (!notification.getReceiver().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.USER_INFO_NOT_MATCHED);
        }
        notificationRepository.deleteById(notificationId);
        return ApiResponseDto.success(SuccessCode.SUCCESS_NOTIFICATION_DELETE.getMessage());
    }

    /**
     * 알림 전체 삭제
     * @param user
     * @return
     */

    @Transactional
    public ApiResponseDto<MessageResponseDto> allDeleteNotification(User user) {
        List<Notification> notificationList = notificationRepository.findAllByReceiver(user);
        notificationRepository.deleteAll(notificationList);
        return ApiResponseDto.success(SuccessCode.SUCCESS_NOTIFICATION_DELETE_ALL.getMessage());

    }

    /**
     * 알림 읽기
     * @param notificationId
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     */
    @Transactional
    public ApiResponseDto<MessageResponseDto> readNotification(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(()
                -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
        // 알람 확인
        notification.confirm();
        notificationRepository.save(notification);
        return ApiResponseDto.success(SuccessCode.SUCCESS_NOTIFICATION_READ.getMessage());
    }

    /**
     * 읽지 않은 알림 조회
     * @param user
     * @return ApiResponseDto<List<NotificationResponseDto>>
     */
    public ApiResponseDto<List<NotificationResponseDto>> getUnreadNotification(User user) {
        List<Notification> notificationList = notificationRepository.findAllByIsReadAndReceiver(false, user);
        return ApiResponseDto.success(SuccessCode.SUCCESS_NOTIFICATION_GET_UNREAD.getMessage(),
                notificationList
                        .stream()
                        .map(NotificationResponseDto::fromEntity)
                        .toList());
    }
}