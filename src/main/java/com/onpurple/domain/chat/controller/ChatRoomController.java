package com.onpurple.domain.chat.controller;

import com.onpurple.domain.chat.dto.ChatMessageResponseDto;
import com.onpurple.domain.chat.dto.ChatRoomDto;
import com.onpurple.domain.chat.dto.ChatMessageRequestDto;
import com.onpurple.domain.chat.service.ChatRoomService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "채팅방 API", description = "채팅방 생성, 채팅방 조회, 채팅방 삭제")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @PostMapping("/room")
    @Operation(summary = "채팅방 생성", description = "채팅방 생성")
    @Parameter(name = "chatMessageRequestDto", description = "채팅방 생성 정보", required = true)
    @Parameter(name = "userDetails", description = "채팅방을 생성할 사용자의 정보", required = true)
    public ChatMessageResponseDto createRoom(@RequestBody ChatMessageRequestDto chatMessageRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.createRoom(chatMessageRequestDto, userDetails.getUser());
    }

    // 사용자 관련 채팅방 전체 조회
    @GetMapping("/rooms")
    @Operation(summary = "사용자 관련 채팅방 전체 조회", description = "사용자 관련 채팅방 전체 조회")
    @Parameter(name = "userDetails", description = "채팅방을 조회할 사용자의 정보", required = true)
    public List<ChatMessageResponseDto> findAllRoomByUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.findAllRoomByUser(userDetails.getUser());
    }

    // 사용자 관련 채팅방 선택 조회
    @GetMapping("/room/{roomId}")
    @Operation(summary = "사용자 관련 채팅방 선택 조회", description = "사용자 관련 채팅방 선택 조회")
    @Parameter(name = "roomId", description = "조회할 채팅방의 id", required = true)
    @Parameter(name = "userDetails", description = "채팅방을 조회할 사용자의 정보", required = true)
    @Parameter(name = "userId", description = "채팅방을 조회할 사용자의 id", required = true)
    public ChatRoomDto findRoom(@PathVariable String roomId,
                                @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long userId) {
        return chatRoomService.findRoom(roomId, userDetails.getUser(), userId);
    }

    // 채팅방 삭제
    @DeleteMapping("/room/{id}")
    @Operation(summary = "채팅방 삭제", description = "채팅방 삭제")
    @Parameter(name = "id", description = "삭제할 채팅방의 id", required = true)
    @Parameter(name = "userDetails", description = "채팅방을 삭제할 사용자의 정보", required = true)
    public ApiResponseDto<MessageResponseDto> deleteRoom(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.deleteRoom(id, userDetails.getUser());
    }
}