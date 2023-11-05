package com.onpurple.domain.admin.repository;

import com.onpurple.domain.chatRoom.model.ChatRoom;
import com.onpurple.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByUserOrReceiver(User user, String receiver);

    ChatRoom findByIdAndUserOrIdAndReceiver(Long id, User user, Long id1, String nickname);

    ChatRoom findBySenderAndReceiver(String nickname, String receiver);

    ChatRoom findByRoomIdAndUserOrRoomIdAndReceiver(String roomId, User user, String roomId1, String nickname);

    ChatRoom findByRoomId(String roomId);
}