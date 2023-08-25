package com.project.date.controller;

import com.project.date.dto.request.ChatMessageDto;
import com.project.date.dto.request.MessageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final SimpMessagingTemplate template;

    /**
     * websocket "/pub/chat/enter"로 들어오는 메시징을 처리한다.
     * 채팅방에 입장했을 경우
     * Client가 send할 수 있는 경로 /pub/chat/enter
     */
    //
    @MessageMapping(value = "/chat/enter")
    public void enter(MessageRequestDto message) {

        if(ChatMessageDto.MessageType.JOIN.equals(message.getType())) {
            message.setMessage(message.getSender()+"님이 입장하셨습니다.");
        }

        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);   // roomId를 topic으로 생성-> roomId로 구분, 메시지 전달

    }


    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    //
    @MessageMapping(value = "/chat/message") //메시지 보내는거야
    public void message(MessageRequestDto message) {

        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);

    }
}