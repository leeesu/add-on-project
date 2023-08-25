package com.project.date.repository;

import com.project.date.model.ChatRoom;
import com.project.date.model.ChatRoomUser;
import com.project.date.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    Page<ChatRoomUser> findAllByUser(User user, Pageable pageable);
    List<ChatRoomUser> findAllByUserNotAndChatRoom(User user, ChatRoom chatRoom);
    void deleteByChatRoomAndUser(ChatRoom chatRoom, User user);
}
