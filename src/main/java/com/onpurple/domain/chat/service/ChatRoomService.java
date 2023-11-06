package com.onpurple.domain.chat.service;

import com.onpurple.domain.chat.repository.ChatRoomRepository;
import com.onpurple.domain.chat.dto.ChatMessageResponseDto;
import com.onpurple.domain.chat.repository.ChatMessageRepository;
import com.onpurple.domain.chat.dto.ChatRoomDto;
import com.onpurple.domain.chat.dto.ChatMessageRequestDto;
import com.onpurple.domain.user.model.User;
import com.onpurple.domain.user.repository.UserRepository;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.enums.ErrorCode;
import com.onpurple.global.exception.CustomException;
import com.onpurple.domain.chat.model.ChatMessage;
import com.onpurple.domain.chat.model.ChatRoom;
import com.onpurple.global.redis.pubsub.RedisSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onpurple.global.enums.RedisKeyEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, ChatMessage> redisTemplateMessage;

    // 채팅방(topic)에 발행되는 메시지 처리하는 리스너
    private final RedisMessageListenerContainer redisMessageListener;

    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;

    // 1. redis
    private static final String Chat_Rooms = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomDto> opsHashMessageRoom;

    // 2. 채팅방의 대화 메시지 발행을 위한 redis topic(채팅방) 정보
    private Map<String, ChannelTopic> topics;

    // 3. redis 의 Hash 데이터 다루기 위함
    @PostConstruct
    private void init() {
        opsHashMessageRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    /**
     * 채팅방 생성
     *
     * @param chatMessageRequestDto
     * @param user
     * @return ChatMessageResponseDto
     */
    public ChatMessageResponseDto createRoom(ChatMessageRequestDto chatMessageRequestDto, User user) {

        // 4.
        ChatRoom chatRoom = chatRoomRepository.findBySenderAndReceiver(user.getNickname(), chatMessageRequestDto.getReceiver());

        // 5. 처음 채팅방 생성 또는 이미 생성된 채팅방이 아닌 경우
        if ((chatRoom == null) || (chatRoom != null && (!user.getNickname().equals(chatRoom.getSender()) && !chatMessageRequestDto.getReceiver().equals(chatRoom.getReceiver())))) {
            ChatRoomDto chatRoomDto = ChatRoomDto.create(chatMessageRequestDto, user);
            opsHashMessageRoom.put(Chat_Rooms, chatRoomDto.getRoomId(), chatRoomDto);      // redis hash 에 채팅방 저장해서, 서버간 채팅방 공유
            chatRoom = chatRoomRepository.save(new ChatRoom(chatRoomDto.getId(), chatRoomDto.getRoomName(), chatRoomDto.getSender(), chatRoomDto.getRoomId(), chatRoomDto.getReceiver(), user));

            return new ChatMessageResponseDto(chatRoom);
            // 6. 이미 생성된 채팅방인 경우
        } else {
            return new ChatMessageResponseDto(chatRoom.getRoomId());
        }
    }


    /**
     * 사용자 관련 채팅방 전체 조회
     *
     * @param user
     * @return List<ChatMessageResponseDto>
     */
    public List<ChatMessageResponseDto> findAllRoomByUser(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserOrReceiver(user, user.getNickname());      // sender & receiver 모두 해당 쪽지방 조회 가능 (1:1 대화)

        List<ChatMessageResponseDto> chatMessageResponseDtos = new ArrayList<>();

        for (ChatRoom chatRoom : chatRooms) {
            //  user 가 sender 인 경우
            if (user.getNickname().equals(chatRoom.getSender())) {
                ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto(
                        chatRoom.getId(),
                        chatRoom.getReceiver(),        // roomName
                        chatRoom.getRoomId(),
                        chatRoom.getSender(),
                        chatRoom.getReceiver());

                // 8. 가장 최신 메시지 & 생성 시간 조회
                ChatMessage latestMessage = chatMessageRepository.findTopByRoomIdOrderByCreatedAtDesc(chatRoom.getRoomId());
                if (latestMessage != null) {
                    chatMessageResponseDto.setLatestChatMessageCreatedAt(latestMessage.getCreatedAt());
                    chatMessageResponseDto.setLatestChatMessageContent(latestMessage.getMessage());
                }

                chatMessageResponseDtos.add(chatMessageResponseDto);
                // user 가 receiver 인 경우
            } else {
                ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto(
                        chatRoom.getId(),
                        chatRoom.getSender(),        // roomName
                        chatRoom.getRoomId(),
                        chatRoom.getSender(),
                        chatRoom.getReceiver());

                // Redis에서 메시지 가져오기
                List<ChatMessage> redisMessageList = redisTemplateMessage.opsForList().range(ChatRoom_KEY + chatRoom.getRoomId(), 0, -1);


                ChatMessage latestMessage;
                if (redisMessageList == null || redisMessageList.isEmpty()) {
                    // Redis에서 가져온 메시지가 없다면, DB에서 메시지 가져오기
                    latestMessage = chatMessageRepository.findTopByRoomIdOrderByCreatedAtDesc(chatRoom.getRoomId());
                } else {
                    // Redis에서 가져온 메시지가 있다면, 가장 최신 메시지를 가져오기
                    latestMessage = redisMessageList.get(redisMessageList.size() - 1);
                }

                chatMessageResponseDto.setLatestChatMessageCreatedAt(latestMessage.getCreatedAt());
                chatMessageResponseDto.setLatestChatMessageContent(latestMessage.getMessage());

                chatMessageResponseDtos.add(chatMessageResponseDto);
            }
        }

            return chatMessageResponseDtos;
    }


    /**
     * 사용자 관련 채팅방 선택 조회
     * @param roomId
     * @param user
     * @param userId
     * @return ChatRoomDto
     */
    public ChatRoomDto findRoom(String roomId, User user, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        // 사용자 조회
        User receiver = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        // 9. sender & receiver 모두 messageRoom 조회 가능
        chatRoom = chatRoomRepository.findByRoomIdAndUserOrRoomIdAndReceiver(roomId, user, roomId, receiver.getNickname());
        if (chatRoom == null) {
            throw new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        ChatRoomDto chatRoomDto = new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getRoomName(),
                chatRoom.getRoomId(),
                chatRoom.getSender(),
                chatRoom.getReceiver());

        return chatRoomDto;
    }

    /**
     * 채팅방 삭제
     * @param id
     * @param user
     * @return ApiResponseDto<MessageResponseDto>
     */
    public ApiResponseDto<MessageResponseDto> deleteRoom(Long id, User user) {
        ChatRoom chatRoom = chatRoomRepository.findByIdAndUserOrIdAndReceiver(id, user, id, user.getNickname());

        // sender 가 삭제할 경우
        if (user.getNickname().equals(chatRoom.getSender())) {
            chatRoomRepository.delete(chatRoom);
            opsHashMessageRoom.delete(Chat_Rooms, chatRoom.getRoomId());
            // receiver 가 삭제할 경우
        } else if (user.getNickname().equals(chatRoom.getReceiver())) {
            chatRoom.setReceiver("Not_Exist_Receiver");
            chatRoomRepository.save(chatRoom);
        }

        return ApiResponseDto.success("채팅방이 삭제되었습니다.");
    }

    // 채팅방 입장
    public void enterMessageRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);

        if (topic == null) {
            topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);        // pub/sub 통신을 위해 리스너를 설정. 대화가 가능해진다
            topics.put(roomId, topic);
        }
    }

    // redis 채널에서 채팅방 조회
    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}