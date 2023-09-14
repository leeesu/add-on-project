package com.onpurple.service;

import com.onpurple.dto.response.ChatRoomResponseDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.exception.CustomException;
import com.onpurple.exception.ErrorCode;
import com.onpurple.model.ChatMessage;
import com.onpurple.model.ChatRoom;
import com.onpurple.model.User;
import com.onpurple.repository.ChatMessageRepository;
import com.onpurple.repository.ChatRoomRepository;
import com.onpurple.repository.LikeRepository;
import com.onpurple.repository.UserRepository;
import com.onpurple.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;


    @Transactional
    public ChatRoomResponseDto createChatRoom(Long userId, User user) {
        User otherUser = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 같은 회원들을 담고 있는 채팅방 검색
        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByUserAndOtherUser(user,otherUser);

        if (existingChatRoom.isPresent()) {
            // 이미 해당 사용자 간의 채팅방이 존재하면 예외를 발생시킨다.
            throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_EXISTS);
        }

        // 내가 좋아요한 회원 리스트 가져오기
        List<Integer> likeList = likeRepository.likeToLikeUserId(user.getId())
                .stream()
                .distinct()
                .collect(Collectors.toList());
        // 그중에서 나를 좋아요한 회원 리스트 가져오기
        List<User> getLikeUser = userRepository.matchingUser(likeList);
        // 서로 좋아요한 회원이 없거나, 다른유저가 나를 좋아한 기록이 없다면 채팅방을 만들 수없다.
        if (getLikeUser.isEmpty() && !(getLikeUser.contains(otherUser.getId()))) {
            throw new CustomException(ErrorCode.MATCHING_NOT_FOUND);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .user(user)
                .otherUser(otherUser)
                .userProfile(user.getImageUrl())
                .otherUserProfile(otherUser.getImageUrl())
                .build();

        ChatRoom createRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponseDto.fromEntity(createRoom);
    }

    public ResponseDto<?> getChatRoom(Long roomId, User user) {
        ChatRoom chatRoom = assertValidateChatRoom(roomId);

        // 채팅방에 존재하는 회원인지 확인
        if (!chatRoom.getUser().equals(user) && !chatRoom.getOtherUser().equals(user)) {
            // 존재하지 않는다면 예외를 발생시긴다.
            throw new CustomException(ErrorCode.USER_NOT_PARTICIPANT);
        }
        return ResponseDto.success(chatRoom);
    }

    // 내가 참여한 채팅방 가져오기
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getRoomsForUser(User user) {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByUserOrOtherUser(user, user);
        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();
        for (ChatRoom chatRoom : chatRoomList) {

            ChatMessage recentMessage = chatMessageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom).orElse(null);
            chatRoomResponseDtoList.add(
                    ChatRoomResponseDto.ListFromEntity(chatRoom, recentMessage)
            );
        }
        return chatRoomResponseDtoList;
    }


    public ResponseDto<?> deleteChatRoom(Long roomId, User user) {
        // 해당 챗방이 존재하는지 확인
        ChatRoom chatRoom = assertValidateChatRoom(roomId);

        // 채팅방에 존재하는 회원인지 확인
        if (!chatRoom.getUser().equals(user) && !chatRoom.getOtherUser().equals(user)) {
            // 존재하지 않는다면 예외를 발생시긴다.
            throw new CustomException(ErrorCode.USER_NOT_PARTICIPANT);
        }
        chatRoomRepository.deleteById(roomId);
        return ResponseDto.success(roomId + "번 채팅방 삭제 성공");

    }

    // 채팅방이 존재하는지 유효성 체크
    public ChatRoom assertValidateChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }




}

