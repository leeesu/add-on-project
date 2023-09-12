package com.onpurple.controller;

import com.onpurple.dto.request.ChatMessageRequestDto;
import com.onpurple.dto.response.ChatRoomResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.ChatMessageService;
import com.onpurple.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/chat/rooms")
    public ChatRoomResponseDto createChatRoom(@RequestParam("userId") Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatRoomService.createChatRoom(userId, userDetails.getUser());
    }

    @GetMapping("/chat/rooms")
    public List<ChatRoomResponseDto> getChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatRoomService.getRoomsForUser(userDetails.getUser());
    }

    @GetMapping("/chat/rooms/{roomId}")
    public ResponseEntity<?> getChatMessages(@PathVariable(name = "roomId") Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return chatMessageService.getMessages(roomId,userDetails.getUser() );
    }

    @MessageMapping("/chat/rooms/{roomId}")
    public void sendMessage(@PathVariable(name = "roomId") Long roomId, @Payload ChatMessageRequestDto chatMessageRequestDto){
        simpMessagingTemplate.convertAndSend("/chat/rooms" + roomId, chatMessageService.sendMessage(roomId, chatMessageRequestDto));
    }
}