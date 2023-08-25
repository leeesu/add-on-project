package com.project.date.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.project.date.dto.request.ChatRoomUserRequestDto;
import com.project.date.dto.response.ChatMessageTestDto;
import com.project.date.dto.response.ChatRoomOtherUserInfoResponseDto;
import com.project.date.dto.response.ChatRoomResponseDto;
import com.project.date.impl.UserDetailsImpl;
import com.project.date.model.ChatMessage;
import com.project.date.model.ChatRoom;
import com.project.date.model.ChatRoomUser;
import com.project.date.model.User;
import com.project.date.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private static int DISPLAY_CHAT_ROOM_COUNT = 10;

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository memberRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisRepository redisRepository;

    //채팅방 생성
    @Transactional
    public String createChatRoom (
            ChatRoomUserRequestDto requestDto,
            UserDetailsImpl userDetails) {
        //상대방 방도 생성 > 상대방 찾기
        if(requestDto.getUserId().equals(userDetails.getUser().getId())) throw new RuntimeException ("자기자신에게 채팅을 신청할 수 없습니다");
        User anotherUser = memberRepository.findById(requestDto.getUserId()).orElseThrow(
                () -> new NotFoundException("상대방을 찾을 수 없습니다.")
        );

        //roomHashCode 만들기
        int roomHashCode = createRoomHashCode(userDetails, anotherUser);

        //방 존재 확인 함수
        if(existRoom(roomHashCode, userDetails, anotherUser)){
            ChatRoom existChatRoom = chatRoomRepository.findByRoomHashCode(roomHashCode).orElseThrow(
                    ()-> new RuntimeException("알 수 없는 채팅방 입니다.")
            );
            return existChatRoom.getChatRoomUuid();
        }

        //방 먼저 생성
        ChatRoom room = new ChatRoom(roomHashCode);
        chatRoomRepository.save(room);

        //내 방
        ChatRoomUser chatRoomUser = new ChatRoomUser(userDetails.getUser(), anotherUser, room);
        //다른 사람 방
        ChatRoomUser chatRoomAnotherUser = new ChatRoomUser(anotherUser, userDetails.getUser(), room);

        //저장
        chatRoomUserRepository.save(chatRoomUser);
        chatRoomUserRepository.save(chatRoomAnotherUser);

        return room.getChatRoomUuid();
    }

    //for 둘 다 있는 방 판단
    public int createRoomHashCode(
            UserDetailsImpl userDetails,
            User anotherUser) {

        Long userId = userDetails.getUser().getId();
        Long anotherId = anotherUser.getId();
        return userId > anotherId ? Objects.hash(userId, anotherId) : Objects.hash(anotherId, userId);
    }

    //이미 방이 존재할 때
    @Transactional
    public boolean existRoom(
            int roomHashCode,
            UserDetailsImpl userDetails,
            User anotherUser) {

        ChatRoom chatRoom = chatRoomRepository.findByRoomHashCode(roomHashCode).orElse(null);

        //방이 존재 할 때
        if (chatRoom != null) {
            List<ChatRoomUser> chatRoomUser = chatRoom.getChatRoomUsers();

            if (chatRoomUser.size() == 1) {
                //나만 있을 때
                if (chatRoomUser.get(0).getUser().getId().equals(userDetails.getUser().getId())) {
                    ChatRoomUser user = new ChatRoomUser(anotherUser, userDetails.getUser(), chatRoom);
                    chatRoomUserRepository.save(user);
                } else {
                    //상대방만 있을 때
                    ChatRoomUser user = new ChatRoomUser(userDetails.getUser(), anotherUser, chatRoom);
                    chatRoomUserRepository.save(user);
                }
            }
            return true;
        }
        return false;
    }

    //채팅방 조회
    public List<ChatRoomResponseDto> getChatRoom(UserDetailsImpl userDetails, int page) {

        // user로 챗룸 유저를 찾고 >> 챗룸 유저에서 채팅방을 찾는다
        // 마지막나온 메시지 ,내용 ,시간
        Pageable pageable = PageRequest.of(page, DISPLAY_CHAT_ROOM_COUNT);
        List<ChatRoomResponseDto> responseDtos = new ArrayList<>();
        Page<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findAllByUser(userDetails.getUser(),pageable);
        //List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findAllByMember(userDetails.getMember());
        int totalCnt = 0;
        for(ChatRoomUser chatRoomUser : chatRoomUsers) {
            String roomUuid = chatRoomUser.getChatRoom().getChatRoomUuid();
            totalCnt += redisRepository.getChatRoomMessageCount(roomUuid, chatRoomUser.getUser().getId());
        }
        for (ChatRoomUser chatRoomUser : chatRoomUsers) {
            ChatRoomResponseDto responseDto = createChatRoomDto(chatRoomUser, totalCnt);
            responseDtos.add(responseDto);

            //정렬
            responseDtos.sort(Collections.reverseOrder());
        }
        return responseDtos;
    }

    public ChatRoomResponseDto createChatRoomDto(ChatRoomUser chatRoomUser, int totalCnt) {
        String otherId = chatRoomUser.getName();
        String otherNickname = chatRoomUser.getOtherNickname();
//        String otherNickname = chatRoomUser.getName();
        String roomUuid = chatRoomUser.getChatRoom().getChatRoomUuid();
        String otherImageUrl = chatRoomUser.getOtherImageUrl();
        String nickname = chatRoomUser.getMyNickname();
        String lastMessage;
        LocalDateTime lastTime;

        //마지막
        List<ChatMessage> Messages = chatMessageRepository.findAllByChatRoomOrderByCreatedAtDesc(chatRoomUser.getChatRoom());
        //메시지 없을 때 디폴트
        if (Messages.isEmpty()) {
            lastMessage = "채팅방이 생성 되었습니다.";
            lastTime = LocalDateTime.now();
        } else {
            lastMessage = Messages.get(0).getMessage();
            lastTime = Messages.get(0).getCreatedAt();
        }

        int unReadMessageCount = redisRepository.getChatRoomMessageCount(roomUuid, chatRoomUser.getUser().getId());

        return new ChatRoomResponseDto(roomUuid, otherId, nickname, otherNickname, otherImageUrl, lastMessage, lastTime, unReadMessageCount ,totalCnt);

    }

    //채팅방 삭제
    @Transactional
    public void deleteChatRoom(ChatRoom chatroom, User user) {
        if (chatroom.getChatRoomUsers().size() != 1) {
            chatRoomUserRepository.deleteByChatRoomAndUser(chatroom, user);
        } else if (chatroom.getChatRoomUsers().size() == 1){
            chatRoomRepository.delete(chatroom);
        }
    }

    //채팅방 입장
    public ChatRoomOtherUserInfoResponseDto getOtherUserInfo(String roomId, UserDetailsImpl userDetails) {
        User myUser = userDetails.getUser();
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomUuid(roomId).orElseThrow(
                () -> new NotFoundException("채팅방을 찾을 수 없습니다.")
        );

        List<ChatRoomUser> users = chatRoom.getChatRoomUsers();

        for(ChatRoomUser user : users){
            if(!user.getUser().getId().equals(myUser.getId())) {
                User otherUser = user.getUser();
                return new ChatRoomOtherUserInfoResponseDto(otherUser);
            }
        }

        User anotherUser = memberRepository.findByUsername(users.get(0).getName()).orElseThrow(
                ()-> new NotFoundException("채팅상대가 존재하지 않습니다.")
        );
        ChatRoomUser anotherChatRoomUser = new ChatRoomUser(anotherUser, myUser, chatRoom);
        chatRoomUserRepository.save(anotherChatRoomUser);

        return new ChatRoomOtherUserInfoResponseDto(anotherUser);

    }

    //채팅방 이전 대화내용 불러오기
    public List<ChatMessageTestDto> getPreviousChatMessage(String roomId, UserDetailsImpl userDetails) {
        List<ChatMessageTestDto> chatMessageTestDtos = new ArrayList<>();

        ChatRoom chatroom = chatRoomRepository.findByChatRoomUuid(roomId).orElseThrow(
                () -> new NotFoundException("채팅방을 찾을 수 없습니다.")
        );

        List<ChatRoomUser> chatRoomUsers = chatroom.getChatRoomUsers();

        //혹시 채팅방 이용자가 아닌데 들어온다면,
        for(ChatRoomUser chatroomUser:chatRoomUsers){
            if(chatroomUser.getUser().getId().equals(userDetails.getUser().getId())) {
                List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomOrderByCreatedAtAsc(chatroom);
                for(ChatMessage chatMessage : chatMessages){
                    chatMessageTestDtos.add(new ChatMessageTestDto(chatMessage));
                }
                return chatMessageTestDtos;
            }
        }
        throw new RuntimeException("접근할 수 없는 채팅방입니다.");
    }
}
