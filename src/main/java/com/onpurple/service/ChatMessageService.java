package com.onpurple.service;

import com.onpurple.dto.request.ChatMessageRequestDto;
import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.model.ChatMessage;
import com.onpurple.model.ChatRoom;
import com.onpurple.model.User;
import com.onpurple.repository.ChatMessageRepository;
import com.onpurple.repository.ChatRoomRepository;
import com.onpurple.dto.response.ChatMessageResponseDto;
import com.onpurple.repository.UserRepository;
import com.onpurple.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public ChatMessageResponseDto sendMessage(Long roomId, ChatMessageRequestDto chatMessageRequestDto) {

        User sender = userRepository.findById(chatMessageRequestDto.getSenderId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
        );
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(chatMessageRequestDto.getMessage())
                .build();

        chatMessageRepository.save(chatMessage);
        return ChatMessageResponseDto.fromEntity(chatMessage);
    }

    public List<ChatMessageResponseDto> getMessages(Long roomId, User user) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND)
        );

        if(!chatRoom.getUser().getId().equals(user.getId()) && !chatRoom.getOtherUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoomOrderByCreatedAtAsc(chatRoom);
        List<ChatMessageResponseDto> chatMessageResponseDtoList = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessageList) {
            chatMessageResponseDtoList.add(ChatMessageResponseDto.fromEntity(chatMessage)
            );
        }

        return chatMessageResponseDtoList;
    }


}
