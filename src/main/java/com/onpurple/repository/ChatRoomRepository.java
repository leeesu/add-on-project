package com.project.date.repository;

import com.project.date.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByRoomHashCode(int roomUsers);
    Optional<ChatRoom> findByChatRoomUuid(String chatRoomUuid);
}