package com.onpurple.service;

import com.onpurple.dto.request.ChatMessageRequestDto;
import com.onpurple.dto.response.ChatMessageResponseDto;
import com.onpurple.model.ChatMessage;
import com.onpurple.model.ChatRoom;
import com.onpurple.model.User;
import com.onpurple.repository.ChatMessageRepository;
import com.onpurple.repository.ChatRoomRepository;
import com.onpurple.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {


    @InjectMocks
    private ChatMessageService chatMessageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    // Create a mock User
    User user = User.builder()
            .id(1L)
            .imageUrl("https://image.url")
            .nickname("")
            .age(44)
            .password("1234567")
            .username("username")
            .build();

    User otherUser = User.builder()
            .id(1L)
            .imageUrl("https://image.url")
            .nickname("nickname11")
            .age(33)
            .password("123456711")
            .username("username11")
            .build();


    // Create a mock ChatRoom
    ChatRoom chatRoom = ChatRoom.builder()
            .id(123L)
            .user(user)
            .otherUser(otherUser)
            .build();

    // Create a mock ChatMessage
    ChatMessage chatMessage = ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(user)
            .message("Hello, world!")
            .build();

    ChatMessageRequestDto requestDto = ChatMessageRequestDto.builder()
            .senderId(chatMessage.getSender().getId())
            .message(chatMessage.getMessage())
            .build();
    @Test
    @DisplayName("채팅 메세지 보내기 테스트")
    public void testSendMessage() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(chatRoomRepository.findById(123L)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        // Call the method to test
        ChatMessageResponseDto responseDto = chatMessageService.sendMessage(123L, requestDto);

        // Verify the result
        assertNotNull(responseDto);
        assertEquals(requestDto.getMessage(), responseDto.getMessage());
        // Add more assertions as needed

    }

    @Test
    @DisplayName("채팅 메세지 가져오기 테스트")
    public void testGetMessages() {
        Long roomId = 123L;

        ChatMessage chatMessage1 = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(user)
                .message("테스트1")
                .build();

        ChatMessage chatMessage2 = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(user)
                .message("테스트2")
                .build();

        List<ChatMessage> chatMessageList = new ArrayList<>();
        chatMessageList.add(chatMessage1);
        chatMessageList.add(chatMessage2);

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findAllByChatRoomOrderByCreatedAtAsc(chatRoom)).thenReturn(chatMessageList);

        List<ChatMessageResponseDto> responseDtoList = chatMessageService.getMessages(roomId, user);

        assertNotNull(responseDtoList);
        assertEquals(2, responseDtoList.size());
        assertEquals("테스트1", responseDtoList.get(0).getMessage());
        assertEquals("테스트2", responseDtoList.get(1).getMessage());
    }

}