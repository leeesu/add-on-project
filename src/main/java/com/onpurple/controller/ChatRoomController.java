package com.onpurple.controller;

import com.onpurple.dto.request.ChatMessageRequestDto;
import com.onpurple.dto.response.ChatMessageResponseDto;
import com.onpurple.dto.response.ChatRoomDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @PostMapping("/room")
    public ChatMessageResponseDto createRoom(@RequestBody ChatMessageRequestDto chatMessageRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.createRoom(chatMessageRequestDto, userDetails.getUser());
    }

    // 사용자 관련 채팅방 전체 조회
    @GetMapping("/rooms")
    public List<ChatMessageResponseDto> findAllRoomByUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.findAllRoomByUser(userDetails.getUser());
    }

    // 사용자 관련 채팅방 선택 조회
    @GetMapping("room/{roomId}")
    public ChatRoomDto findRoom(@PathVariable String roomId,
                                @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId) {
        return chatRoomService.findRoom(roomId, userDetails.getUser(), userId);
    }

    // 채팅방 삭제
    @DeleteMapping("room/{id}")
    public ResponseDto deleteRoom(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.deleteRoom(id, userDetails.getUser());
    }
}