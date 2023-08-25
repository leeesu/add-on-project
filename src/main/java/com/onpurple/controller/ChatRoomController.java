package com.project.date.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.project.date.dto.request.ChatRoomUserRequestDto;
import com.project.date.dto.response.ChatMessageTestDto;
import com.project.date.dto.response.ChatRoomOtherUserInfoResponseDto;
import com.project.date.dto.response.ChatRoomResponseDto;
import com.project.date.impl.UserDetailsImpl;
import com.project.date.model.ChatRoom;
import com.project.date.repository.ChatRoomRepository;
import com.project.date.repository.RedisRepository;
import com.project.date.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final RedisRepository redisRepository;
    private final ChatRoomRepository chatRoomRepository;

    //방생성
    @PostMapping ("/rooms")
    public String createChatRoom(
            @RequestBody ChatRoomUserRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String chatRoomUuid = chatRoomService.createChatRoom(requestDto, userDetails);
        Long chatPartnerUserId = requestDto.getUserId();
        Long myUserId = userDetails.getUser().getId();

        System.out.println(userDetails.getUser().getUsername()+ "채팅방 생성 요청");
        // redis repository에 채팅방에 존재하는 사람 마다 안 읽은 메세지의 갯수 초기화
        redisRepository.initChatRoomMessageInfo(chatRoomUuid, myUserId);
        redisRepository.initChatRoomMessageInfo(chatRoomUuid, chatPartnerUserId);

        return chatRoomUuid;
    }

    //내가 가진 채팅방 조회
    @GetMapping ("/rooms/{page}")
    public List<ChatRoomResponseDto> getChatRoom (@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @PathVariable int page
    ) {
        page -= 1;
        System.out.println(userDetails.getUser().getUsername()+ "채팅방 조회 요청");
        return chatRoomService.getChatRoom(userDetails, page);
    }

    //채팅방 삭제
    @DeleteMapping("/rooms/{roomId}")
    public void deleteChatRoom(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        //roomId=uuid
        //방번호랑 나간 사람
        System.out.println(roomId+"삭제 요청");
        System.out.println(userDetails.getUser().getUsername()+"삭제 요청");
        ChatRoom chatroom = chatRoomRepository.findByChatRoomUuid(roomId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 채팅방입니다.")
        );

        chatRoomService.deleteChatRoom(chatroom, userDetails.getUser());
    }

    //이전 채팅 메시지 불러오기
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageTestDto> getPreviousChatMessage(@PathVariable String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println(userDetails.getUser().getUsername()+ "채팅방 이전채팅불러오기 요청");
        return chatRoomService.getPreviousChatMessage(roomId, userDetails);
    }

    //채팅방 입장
    @GetMapping("/rooms/enter/{roomId}")
    public ChatRoomOtherUserInfoResponseDto getOtherUserInfo(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){

        System.out.println(userDetails.getUser().getUsername()+ "채팅방 상대방정보 요청");
        return chatRoomService.getOtherUserInfo(roomId, userDetails);
    }
}

